package com.wenkrang.famara.command;

import com.wenkrang.famara.lib.Translation;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record FaCommand(
        String subcommand,
        CommandArgument[] arguments,
        BiConsumer<CommandSender, List<String>> action
) {
    private static final List<FaCommand> subs = new ArrayList<>();

    public static void register(FaCommand command) {
        Stream.of(command.arguments())
                .limit(command.arguments().length - 1)
                .filter(i -> i.test(""))
                .findFirst()
                .ifPresentOrElse(
                        ignored ->
                                Bukkit.getConsoleSender().sendMessage(String.format(
                                        Translation.CURRENT.of("invalidNullable"),
                                        command.subcommand()
                                        )),
                        () -> subs.add(command));
    }

    public static CommandExecutor exec = (
            sender,
            cmd,
            s,
            args
    ) -> subs.stream()
            .filter(c ->
                    // 确保数组长度足够（不会出现越界）
                    // 对于空数组情况，arguments 的长度是自然数，该条件不成立
                    // args 相比 c.arguments 和最终传入的参数列表多一个子命令
                    args.length > Stream.of(c.arguments())
                            // 去可空参数
                            .filter(i -> !i.test(""))
                            .count() &&
                    // 匹配子命令
                    args[0].equals(c.subcommand()) &&
                    // 对每个参数进行精细匹配
                    IntStream.range(0, c.arguments().length)
                            .allMatch(i -> c.arguments()[i].test(args[i + 1]))
            )
            .findFirst()
            .map(i -> {
                try {
                    i.action.accept(sender, Stream.of(args).skip(1).toList());
                } catch (IllegalArgumentException ignored) {
                    sender.sendMessage(Translation.CURRENT.of("useInGame"));
                } catch (Exception e) {
                    // 报错
                    sender.sendMessage(String.format(
                            Translation.CURRENT.of("unknownError"),
                            e.getLocalizedMessage()));
                }
                return true;
            })
            .orElseGet(() -> {
                sender.sendMessage(Translation.CURRENT.of("lostArguments"));
                getHelp(sender);
                return false;
            });

    public static TabCompleter tabCompleter = (
            sender,
            cmd,
            s,
            args
    ) -> args.length == 0 ? subs.stream().map(FaCommand::subcommand).toList() :
            subs.stream()
                    .filter(i -> args[0].equals(i.subcommand()))
                    .findFirst()
                    .filter(i -> args.length <= i.arguments().length)
                    .map(i -> i.arguments()[args.length - 1].describe())
                    .orElse(null);

    public static void getHelp(@NotNull CommandSender commandSender) {
        commandSender.sendMessage(Translation.CURRENT.of("help1"));
        commandSender.sendMessage(Translation.CURRENT.of("help2"));
        commandSender.sendMessage(Translation.CURRENT.of("help3"));
        commandSender.sendMessage(Translation.CURRENT.of("help4"));
        commandSender.sendMessage(Translation.CURRENT.of("help5"));
        commandSender.sendMessage(Translation.CURRENT.of("help6"));
        commandSender.sendMessage(Translation.CURRENT.of("help7"));
    }
}
