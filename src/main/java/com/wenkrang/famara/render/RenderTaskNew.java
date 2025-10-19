package com.wenkrang.famara.render;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 渲染任务类，用于处理图像渲染过程中的位置跟踪和状态管理
 */
public class RenderTaskNew {
    int x;
    int y;
    Location eyes;
    double pitch;
    double yaw;
    double fieldOfView;
    String uuid;
    BufferedImage image;
    Player player;
    File picture;

    /**
     * 构造一个新的渲染任务
     *
     * @param x 渲染任务的起始x坐标
     * @param y 渲染任务的起始y坐标
     * @param eyes 观察者的眼睛位置
     * @param pitch 观察者的俯仰角度
     * @param yaw 观察者的偏航角度
     * @param fieldOfView 视野范围
     * @param uuid 任务唯一标识符
     * @param image 要渲染的图像缓冲区
     * @param player 相关的玩家对象
     * @param picture 图像文件
     */
    public RenderTaskNew(
            int x,
            int y,
            Location eyes,
            double pitch,
            double yaw,
            double fieldOfView,
            String uuid,
            BufferedImage image,
            Player player,
            File picture
    ) {
        this.x = x;
        this.y = y;
        this.eyes = eyes;
        this.pitch = pitch;
        this.yaw = yaw;
        this.fieldOfView = fieldOfView;
        this.uuid = uuid;
        this.image = image;
        this.player = player;
        this.picture = picture;
    }

    /**
     * 渲染任务执行步骤
     */
    public void step(){
        RenderLib.render(x, y, eyes, pitch, yaw, fieldOfView, uuid, image, player, picture);

        x++;
        // 当x坐标超出范围时，重置x坐标并增加y坐标
        if (x >= 128) {
            x = 0;
            y++;
        }
    }

    /**
     * 检查渲染任务是否完成
     *
     * @return 当y坐标达到或超过128时返回true，否则返回false
     */
    public boolean isFinished(){
        return y >= 128;
    }

}
