package com.wenkrang.famara.command;

import com.wenkrang.famara.lib.Translation;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 用于创建Famara命令的类
 *
 * @param subcommand 子命令名称
 * @param arguments 子命令参数列表
 * @param action 子命令处理器
 */
public record FaCmd(
        String subcommand,
        CmdArgs[] arguments,
        //接受两个参数的Lambda
        BiConsumer<CommandSender, List<String>> action
) {
    //子命令列表
    private static final List<FaCmd> subs = new ArrayList<>();

    /**
     * 用于注册Famara命令
     *
     * @param command 需要注册的Famara命令
     */
    public static void register(FaCmd command) {
        //流式化命令参数
        Stream.of(command.arguments())
                //限制流项数量，去掉最后一个项（如果有参数）
                .limit(command.arguments().length > 0 ? command.arguments().length - 1 : 0)
                //过滤 非法 空参数

                //说白了就是 防呆 机制
                //目前的命令系统 只 接受最后一个参数为空
                //因此用该段代码来检测 是否 除了最后一个参数 其他参数不接受为空

                //这里过滤出 除了最后一个参数 其他也能接受为空的参数
                .filter(i -> i.test(""))
                //如果有非法为空参数，整个命令直接忽略

                //由于存在一个非法参数就忽略整个命令，所以直接获取第一个非法参数
                .findFirst()
                //判断非空
                .ifPresentOrElse(
                        //在控制台提示忽略
                        ignored ->
                                Bukkit.getConsoleSender().sendMessage(String.format(
                                        Translation.CURRENT.of("invalidNullable"),
                                        command.subcommand()
                                        )),
                        //正常添加
                        () -> subs.add(command));
    }

    //命令执行器，直接在这里定义Bukkit的命令处理器
    public static CommandExecutor exec = (
            sender, //发送命令者
            cmd, //Bukkit命令
            s, //命令名
            args //命令参数
    ) -> subs.stream() //流式化处理命令
            //过滤可空参数
            .filter(c -> {
                //提取不空参数
                final List<CmdArgs> nonNull = Stream.of(c.arguments())
                        //去可空参数
                        .filter(i -> !i.test(""))
                        .toList();

                        //确保传入参数长度足够（不会出现越界）
                        //对于空数组情况，arguments 的长度是自然数，该条件不成立
                        //args 相比 c.arguments 和最终传入的参数列表多一个子命令
                return args.length > nonNull.size() &&
                        //匹配 子命令
                        args[0].equalsIgnoreCase(c.subcommand) &&

                        //检查 命令传入的参数 是否匹配 该子命令的 参数
                        IntStream.range(0, nonNull.size())
                                .allMatch(i -> nonNull.get(i).test(args[i + 1]));
                //在这里，如果找到匹配的子命令，就返回true,之后filter将过滤其他不符合的子命令

            })
            //只获取第一个匹配的命令（防止出现重复）
            //如果没有匹配命令，看下方
            .findFirst()
            .map(i -> {
                try {
                    //执行子命令的逻辑
                    //这里过滤掉第一项是因为原传入参数第一项是子命令名称
                    i.action.accept(sender, Stream.of(args).skip(1).toList());
                } catch (Exception e) {
                    //报错
                    sender.sendMessage(String.format(
                            Translation.CURRENT.of("unknownError"),
                            e.getLocalizedMessage()));
                }
                //这里是Bukkit CommandExecutor的返回，直接结束命令逻辑
                return true;
            })
            //如果上方没有匹配命令，直接报错未找到参数
            .orElseGet(() -> {
                //找不到参数
                sender.sendMessage(Translation.CURRENT.of("lostArguments"));
                //返回帮助列表
                getHelp(sender);
                return false;
            });

    //Bukkit 命令补全器
    public static TabCompleter tabCompleter = (
            sender,
            cmd,
            s,
            args
            // args 呈现的：<sub command>, <arg1>, <arg2>, ...
            // 这里所有的元素如果是空格也会被呈现
    ) -> args.length == 1 ?
            subs.stream().map(FaCmd::subcommand).toList()
            //如果命令参数数量为一（即在输入子命令名称过程），直接返回子命令列表
            :
            //如果命令参数超过一，即正在输入参数
            //流式化
            subs.stream()
                    //匹配输入的子命令，并返回对应的参数列表
                    .filter(i -> args[0].equalsIgnoreCase(i.subcommand()) &&
                            //检查传入命令参数数量是否在需要的参数数量之内
                            //这里-2，
                            //第一个-1是因为args从子命令名称开始
                            //第二个-1是考虑可空参数，可空参数均为最后一项
                            i.arguments().length > args.length - 2)
                    .findFirst()
                    //这里的-2是因为索引从0和args第一为子命令
                    .map(i -> {
                        //这里顺便给命令输入加个音效
                        if (sender instanceof Player player) player.playEffect(player.getLocation(), Effect.CLICK1, 1);
                        return i.arguments()[args.length - 2].tabDescribe();
                    })
                    //找不到对应的参数列表直接返空
                    .orElse(null);

    public static void getHelp(@NotNull CommandSender commandSender) {
        //TODO:这里实在太石山了，可以改成自动生成（？
        commandSender.sendMessage(Translation.CURRENT.of("help1"));
        commandSender.sendMessage(Translation.CURRENT.of("help2"));
        commandSender.sendMessage(Translation.CURRENT.of("help3"));
        commandSender.sendMessage(Translation.CURRENT.of("help4"));
        commandSender.sendMessage(Translation.CURRENT.of("help5"));
        commandSender.sendMessage(Translation.CURRENT.of("help6"));
        commandSender.sendMessage(Translation.CURRENT.of("help7"));
    }
}
