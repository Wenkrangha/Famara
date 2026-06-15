package com.wenkrang.famara.render;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 RenderTask.applyColorContrast 的对比度计算。
 *
 * <p>这些测试针对纯整数/浮点运算的 applyColorContrast(int,float,float)，不依赖 Bukkit，
 * 因此可以脱离运行中的服务器执行。
 */
class RenderTaskContrastTest {

    private static int red(int argb)   { return (argb >> 16) & 0xFF; }
    private static int green(int argb) { return (argb >> 8) & 0xFF; }
    private static int blue(int argb)  { return argb & 0xFF; }

    private static int rgb(int r, int g, int b) {
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    @Nested
    @DisplayName("contrast=0 时为恒等变换（回归保护）")
    class Identity {

        @Test
        @DisplayName("contrast 为 0 时输出 RGB 等于输入")
        void contrastZero_isIdentity() {
            int input = rgb(30, 130, 200);

            int out = RenderTask.applyColorContrast(input, 0f, 70f);

            assertEquals(30, red(out));
            assertEquals(130, green(out));
            assertEquals(200, blue(out));
        }

        @Test
        @DisplayName("任意亮度下 contrast=0 仍为恒等变换")
        void contrastZero_brightnessIrrelevant() {
            int input = rgb(10, 20, 30);

            assertEquals(input & 0xFFFFFF, RenderTask.applyColorContrast(input, 0f, 0f) & 0xFFFFFF);
            assertEquals(input & 0xFFFFFF, RenderTask.applyColorContrast(input, 0f, 255f) & 0xFFFFFF);
        }
    }

    @Nested
    @DisplayName("通道被钳制在 0-255，不污染相邻通道")
    class ChannelClamping {

        @Test
        @DisplayName("绿通道溢出不污染红通道")
        void greenOverflow_doesNotCorruptRed() {
            // newG = (255 - 0) * (1 + 1) + 0 = 510，未 clamp 时高位会串入红通道
            int input = rgb(0, 255, 0);

            int out = RenderTask.applyColorContrast(input, 1f, 0f);

            assertEquals(0, red(out), "红通道输入为 0，绿通道的溢出不应使其变为非 0");
            assertEquals(255, green(out), "绿通道应被钳制到 255");
        }

        @Test
        @DisplayName("负值结果应被钳制为 0 而非环绕")
        void negativeResult_clampedToZero() {
            // newR = (0 - 200) * (1 + 1) + 200 = -200
            int input = rgb(0, 100, 100);

            int out = RenderTask.applyColorContrast(input, 1f, 200f);

            assertEquals(0, red(out), "红通道应被钳制为 0，实际为 " + red(out));
        }
    }

    @Nested
    @DisplayName("contrast 非 0 时确实改变像素（修复前为空操作）")
    class ContrastTakesEffect {

        @Test
        @DisplayName("小数 contrast=0.5 不再被抹成 0，输出与输入不同")
        void fractionalContrast_isNotNoOp() {
            // 修复前：finish() 传入 0.5f 被 (int) 截断为 0 → 空操作
            // 中间调像素，brightness 取灰度中点，contrast 拉伸应明显改变其值
            int input = rgb(30, 130, 200);

            int out = RenderTask.applyColorContrast(input, 0.5f, 128f);

            assertTrue((out & 0xFFFFFF) != (input & 0xFFFFFF),
                    "contrast=0.5 应实际改变像素，而非保持原值");
            // 暗于中点的分量应被进一步压暗
            assertTrue(red(out) < 30, "低于 brightness 的红通道应被对比度进一步压暗");
        }
    }
}
