package com.wenkrang.famara.render.lib;

import java.awt.*;

/**
 * 颜色类，用于存储和管理RGB颜色信息
 * <p>
 * 该类封装了红(R)、绿(G)、蓝(B)三个颜色通道，每个通道的取值范围为0-255。
 * 主要用于照片渲染系统中的颜色数据处理。
 *
 * 警告：该颜色类会自动将颜色数据进行钳制，保证其范围在0-255之间。
 */
public class FaColor {
    public FaColor(int r,int g,int b) {
        this.r = Math.min(Math.max(r, 0), 255);
        this.g = Math.min(Math.max(g, 0), 255);
        this.b = Math.min(Math.max(b, 0), 255);
    }

    /** 红色通道值 (0-255) */
    private int r;
    
    /** 绿色通道值 (0-255) */
    private int g;
    
    /** 蓝色通道值 (0-255) */
    private int b;

    /**
     * 设置红色通道值
     * <p>
     * 如果输入值超出0-255范围，会自动钳制到最接近的有效值。
     *
     * @param R 红色值，建议范围为0-255
     */
    public void setR(int R) {
        if (R < 0) {
            r = 0;
        } else if (R > 255) {
            r = 255;
        } else {
            r = R;
        }
    }

    /**
     * 设置绿色通道值
     * <p>
     * 如果输入值超出0-255范围，会自动钳制到最接近的有效值。
     *
     * @param G 绿色值，建议范围为0-255
     */
    public void setG(int G) {
        if (G < 0) {
            g = 0;
        } else if (G > 255) {
            g = 255;
        } else {
            g = G;
        }
    }

    /**
     * 设置蓝色通道值
     * <p>
     * 如果输入值超出0-255范围，会自动钳制到最接近的有效值。
     *
     * @param B 蓝色值，建议范围为0-255
     */
    public void setB(int B) {
        if (B < 0) {
            b = 0;
        } else if (B > 255) {
            b = 255;
        } else {
            b = B;
        }
    }

    /**
     * 获取红色通道值
     *
     * @return 红色值 (0-255)
     */
    public int getR() {
        return r;
    }

    /**
     * 获取绿色通道值
     *
     * @return 绿色值 (0-255)
     */
    public int getG() {
        return g;
    }

    /**
     * 获取蓝色通道值
     *
     * @return 蓝色值 (0-255)
     */
    public int getB() {
        return b;
    }

    /**
     * 将颜色数据转换为ARGB格式
     * @return ARGB格式的整数
     */
    public int toARGB() {
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    public void set(Color color) {
        setR(color.getRed());
        setG(color.getGreen());
        setB(color.getBlue());
    }

    public void set(FaColor other) {
        this.r = other.r;
        this.g = other.g;
        this.b = other.b;
    }

    public Color toColor() {
        return new Color(r, g, b);
    }

    public void set(int r,int g,int b) {
        setR(r);
        setG(g);
        setB(b);
    }

    /**
     * 批量调整RGB值（零GC优化）
     * @param deltaR 红色增量
     * @param deltaG 绿色增量
     * @param deltaB 蓝色增量
     */
    public void addRGB(int deltaR, int deltaG, int deltaB) {
        this.r = Math.min(Math.max(this.r + deltaR, 0), 255);
        this.g = Math.min(Math.max(this.g + deltaG, 0), 255);
        this.b = Math.min(Math.max(this.b + deltaB, 0), 255);
    }

    /**
     * 批量缩放RGB值（零GC优化）
     * @param factor 缩放因子 (0.0-1.0+)
     */
    public void scaleRGB(float factor) {
        this.r = Math.min(Math.max((int)(this.r * factor), 0), 255);
        this.g = Math.min(Math.max((int)(this.g * factor), 0), 255);
        this.b = Math.min(Math.max((int)(this.b * factor), 0), 255);
    }

    /**
     * 应用光照因子（零GC优化）
     * @param baseFactor 基础亮度因子 (0.0-1.0)
     * @param lightFactor 光照因子 (0.0-1.0)
     */
    public void applyLightFactor(float baseFactor, float lightFactor) {
        float combinedFactor = baseFactor + (1.0f - baseFactor) * lightFactor;
        this.r = Math.min(Math.max((int)(this.r * combinedFactor), 0), 255);
        this.g = Math.min(Math.max((int)(this.g * combinedFactor), 0), 255);
        this.b = Math.min(Math.max((int)(this.b * combinedFactor), 0), 255);
    }
}
