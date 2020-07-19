package cn.innc11.peppershop.command.subcommand;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.command.PluginCommand;
import cn.innc11.peppershop.command.SubCommand;
import cn.innc11.peppershop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.TextFormat;

public class StatusCommand implements SubCommand
{
    @Override
    public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
    {
        if (!(sender instanceof Player && !sender.isOp()))
        {
            int total = PepperShop.ins.hologramListener.getCurrentQueueCapacity();

            PepperShop.scheduler.scheduleDelayedTask(new PluginTask<PepperShop>(PepperShop.ins){
                @Override
                public void onRun(int currTick)
                {
                    long sum = 0;
                    int min = Integer.MAX_VALUE;
                    int max = 0;
                    int sample = 0;

                    for (int i=300;i>0;i--)
                    {
                        int cur = PepperShop.ins.hologramListener.getCurrentQueueCount();
                        sum += cur;
                        min = Math.min(min, cur);
                        max = Math.max(max, cur);
                        sample ++;

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        sender.sendMessage(TextFormat.colorize(String.format("PepperShop队列容量信息(%d):", sample)));
                        Thread.sleep(300);
                        sender.sendMessage(TextFormat.colorize(String.format("平均值: &e%d&r / &b%d", sum/50, total)));
                        Thread.sleep(900);
                        sender.sendMessage(TextFormat.colorize(String.format("峰值(最低/最高): &2%d&r, &c%d", min, max)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }, 0, true);

            sender.sendMessage(TextFormat.colorize("正在进行采样请稍等.."));
        }

        return true;
    }

    @Override
    public String getSubCommandName()
    {
        return "status";
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"sta", "st"};
    }

    @Override
    public CommandParameter[] getSubParameters()
    {
        return null;
    }
}
