package com.wenkrang.famara.command;

import com.wenkrang.faClip.module.faBootstrap.Fa;
import com.wenkrang.faClip.module.faCommand.annotation.*;
import com.wenkrang.faClip.module.faCommand.helper.FaHelperGenerator;
import com.wenkrang.faClip.module.faCommand.interpreter.FaCmdContext;
import com.wenkrang.faClip.module.faInterface.param.bukkitParam.specialDesProvider.BlockDes;
import com.wenkrang.faClip.module.faMessage.Fm;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.lib.ColorManager;
import com.wenkrang.famara.lib.ConsoleLogger;
import com.wenkrang.famara.lib.Translation;
import com.wenkrang.famara.loader.LoadResourcePack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import static com.wenkrang.famara.Famara.excludingBlocksInv;
import static com.wenkrang.famara.Famara.renderService;

public class FCmd {
    @Cmd("fa.help")
    @Help("获取帮助")
    public static void help(FaCmdContext ctx) {
        new FaHelperGenerator(Fa.cmd()).generate("fa").forEach(ctx.sender()::sendMessage);
    }
    
    // 根节点
    @Cmd("fa")
    @Help("Famara 相机命令")
    @OnlyForHelp
    public static void fa() {}
    
    @Cmd("fa.color")
    @Help("获取缺失方块颜色数量")
    public static void color(FaCmdContext ctx) {
        if (ctx.sender().isOp()) ConsoleLogger.info("缺失方块颜色数量： " + ColorManager.excludingBlocks.size());
    }
    
    @Cmd("fa.inv")
    @Help("以任务栏视图查看缺失方块")
    @ForPlayer
    @RequireOP
    public static void inv(FaCmdContext ctx) {
        ((Player) ctx.sender()).openInventory(excludingBlocksInv);
    }

    @Cmd("fa.set")
    @Help("设置缺失方块颜色 <方块名> <R> <G> <B>")
    @RequireOP
    public static void set(FaCmdContext ctx, @CustomDes(BlockDes.class) String name, int R, int G, int B) {
        try {
            ColorManager.setColor(name, R, G, B);
        } catch (NullPointerException e) {
            ctx.sender().sendMessage(Translation.CURRENT.of("setError1"));
        }
        ctx.sender().sendMessage(Translation.CURRENT.of("setSuccessfully"));
    }

    @Cmd("fa.speed")
    @Help("设置渲染速度 <speed>")
    @RequireOP
    public static void speed(FaCmdContext ctx,int speed) {
        renderService.speed = speed;

        if (renderService.speed > 500)
            ctx.sender().sendMessage(Translation.CURRENT.of("warning1"));

        ctx.sender().sendMessage(String.format(
                Translation.CURRENT.of("speedSetSuccessfully"), renderService.speed));
    }

    @Cmd("fa.resource")
    @Help("重新下载资源包")
    @ForPlayer
    public static void resource(FaCmdContext ctx) {
        LoadResourcePack.load((Player) ctx.sender());
    }

    @Cmd("fa.guide")
    @Help("获取合成指南")
    @ForPlayer
    public static void guide(FaCmdContext ctx) {
        Player player = (Player) ctx.sender();
        player.getWorld().dropItem(player.getLocation(), RecipeBook.RecipeBookItem);
    }

    @Cmd("fa.step")
    @Help("逐步设置颜色 <R> <G> <B>")
    @RequireOP
    public static void step(FaCmdContext ctx, int R, int G, int B) {
        try {
            ColorManager.setColor(ColorManager.excludingBlocks.get(0), R, G, B);
            Fm.info(ctx.sender() ,"已设置颜色：" + ColorManager.excludingBlocks.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
