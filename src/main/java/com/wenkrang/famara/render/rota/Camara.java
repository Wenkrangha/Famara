package com.wenkrang.famara.render.rota;

import com.wenkrang.famara.render.rota.lib.Point;
import com.wenkrang.famara.render.rota.lib.RotateHelper;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Camara {
    /**
     * 相机基本信息
     */
    private Location BukkitLoc;
    private Vector BukkitVector;

    // 简化位置信息
    private Point position;
    // 视野
    private double fov;
    // 焦距
    private double focalLength;

    // 分辨率
    private int width;
    private int height;

    /**
     * 计算信息
     */
    private double viewHeight;
    // 视平面四个顶点
    private Point[] vertexes = new Point[4];

    // 预计算的视线方向矩阵, 每个像素对应一条已归一化的射线
    private Vector[][] vectorMatrix;

    // 像素间距
    private double dotPixel;

    public Camara(Location loc, double fov, double focalLength, int width, int height) {
        // 初始化信息
        this.width = width;
        this.height = height;
        this.fov = fov;
        this.focalLength = focalLength;
        BukkitLoc = loc;
        BukkitVector = loc.getDirection();

        position = new Point(loc);

        vectorMatrix = new Vector[width][height];

        init();
    }

    /**
     * 获取指定像素的视线方向(已归一化)。
     *
     * <p>返回的是矩阵内部缓存的对象, 调用方只应读取其分量, 不要直接修改或旋转它。
     *
     * @param x 像素列(0 ~ width-1)
     * @param y 像素行(0 ~ height-1, 0 为画面顶部)
     */
    public Vector getDirection(int x, int y) {
        return vectorMatrix[x][y];
    }

    private void init() {
        viewHeightCalculate();

        vertexesCalculate();

        GenerateVectorMatrix();
    }

    /**
     * 生成视线方向矩阵。
     *
     * <p>先在相机局部坐标系里算出每个像素的方向, 再交给 Bukkit 的 rotateAround 变换到世界坐标。
     * 相机局部坐标系为: +X=画面左方, +Y=画面上方, +Z=视线前方。
     *
     * <p>三处约定缺一不可, 否则画面朝向会错乱:
     * <ul>
     *   <li>rotateAroundY 必须传 <b>-yaw</b>: Minecraft 的 yaw 顺时针增大(左手系),
     *       而 Bukkit 的 rotateAroundY 是标准右手系旋转, 直接传 yaw 会左右镜像。</li>
     *   <li>rotateAroundX 传 <b>+pitch</b> 即可: Bukkit 的 pitch 正值朝下,
     *       而 rotateAroundX(pitch) 把 (0,0,1) 的 y 分量变成 -sin(pitch), 两者吻合。</li>
     *   <li>必须<b>先 X 后 Y</b>: 颠倒顺序无法同时满足 yaw 与 pitch。</li>
     * </ul>
     */
    private void GenerateVectorMatrix() {
        dotPitchCalculate();

        // 旋转角在循环外算好; yaw 取负以抵消 Minecraft 与 Bukkit 旋转方向的差异
        double pitchRad = RotateHelper.angleToRad(BukkitLoc.getPitch());
        double yawRad = RotateHelper.angleToRad(-BukkitLoc.getYaw());

        for (int i = 0; i < width; i++) {
            // 像素中心相对画面中心的横向偏移, 半像素修正保证画面严格居中
            double offsetX = (i + 0.5 - width / 2.0) * dotPixel;

            for (int j = 0; j < height; j++) {
                // 图像 y 轴向下而画面上方为正, 因此取反
                double offsetY = (height / 2.0 - (j + 0.5)) * dotPixel;

                // 局部 X 取负: 经上述旋转后相机 +X 落在世界的"左方"
                vectorMatrix[i][j] = new Vector(-offsetX, offsetY, focalLength)
                        .normalize()
                        .rotateAroundX(pitchRad)
                        .rotateAroundY(yawRad);
            }
        }
    }

    private void dotPitchCalculate() {
        // 横纵使用同一间距, 保证像素为正方形
        dotPixel = viewHeight * 2 / height;
    }

    private void viewHeightCalculate() {
        // fov 为竖直方向的完整视野角(弧度), 取绝对值
        viewHeight = Math.abs(focalLength * Math.tan(fov / 2));
    }

    // 1~4顶点按照坐标系四象限排序
    private void vertexesCalculate() {
        vertexes[0] = new Point(position.getX() + viewHeight, position.getY() + viewHeight, position.getZ() + focalLength);
        vertexes[1] = new Point(position.getX() - viewHeight, position.getY() + viewHeight, position.getZ() + focalLength);
        vertexes[2] = new Point(position.getX() - viewHeight, position.getY() - viewHeight, position.getZ() + focalLength);
        vertexes[3] = new Point(position.getX() + viewHeight, position.getY() - viewHeight, position.getZ() + focalLength);
    }
}
