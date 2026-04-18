package ivangka.cliorderexecutor.cli;

import ivangka.cliorderexecutor.exception.*;
import ivangka.cliorderexecutor.model.ConditionalOrder;
import ivangka.cliorderexecutor.model.LimitOrder;
import ivangka.cliorderexecutor.model.Position;
import ivangka.cliorderexecutor.model.WalletBalance;
import ivangka.cliorderexecutor.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.fusesource.jansi.Ansi.ansi;

import java.util.List;
import java.util.Scanner;

@Component
public class CliHandler {

    private final OrderService orderService;

    @Autowired
    public CliHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        printHeader();

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();
            String[] commandParts = command.trim().split("\\s+");

            try {
                executeCommand(commandParts);
            } catch (InvalidCommandException | BadRetCodeException | OrderNotFoundException |
                     TooSmallOrderSizeException e) {
                System.out.println(ansi().fgBrightRed().a("  " + e.getMessage()).reset());
            } catch (Exception e) {
                System.out.println(ansi().fgBrightRed().a("  Unexpected exception: "
                        + e.getClass().getSimpleName()).reset());
            }
        }
    }

    private void printHeader() {
        String title = "CLI Order Executor";
        String version = "v1.19.8";
        int width = 30;

        String CYAN_256 = "\u001B[38;5;51m";
        String RESET = "\u001B[0m";

        String indent = "  ";

        System.out.println();

        // top border
        System.out.println(indent +
                ansi().fgBright(org.fusesource.jansi.Ansi.Color.CYAN)
                        .a("╔" + "═".repeat(width - 2) + "╗")
                        .reset());

        // title line
        int titlePadding = width - 4 - title.length();
        System.out.println(indent +
                ansi().fgBright(org.fusesource.jansi.Ansi.Color.CYAN).a("║").reset()
                + "  "
                + ansi().fgBright(org.fusesource.jansi.Ansi.Color.YELLOW).a(title).reset()
                + " ".repeat(titlePadding)
                + ansi().fgBright(org.fusesource.jansi.Ansi.Color.CYAN).a("║").reset());

        // version line
        int versionPadding = width - 4 - version.length();
        String versionText = CYAN_256 + version + RESET;

        System.out.println(indent +
                ansi().fgBright(org.fusesource.jansi.Ansi.Color.CYAN).a("║").reset()
                + "  "
                + versionText
                + " ".repeat(versionPadding)
                + ansi().fgBright(org.fusesource.jansi.Ansi.Color.CYAN).a("║").reset());

        // bottom border
        System.out.println(indent +
                ansi().fgBright(org.fusesource.jansi.Ansi.Color.CYAN)
                        .a("╚" + "═".repeat(width - 2) + "╝")
                        .reset());

        System.out.println();
    }

    private void executeCommand(String[] commandParts)
            throws InvalidCommandException, BadRetCodeException, OrderNotFoundException, TooSmallOrderSizeException,
            InterruptedException {
        if (commandParts.length == 0 || commandParts[0].isEmpty()) {
            return;
        }

        // formatting symbol
        if (commandParts.length > 1) {
            String symbol = commandParts[1].toUpperCase();
            commandParts[1] = symbol.endsWith("USDT") ? symbol
                    : symbol.endsWith("USDC") ? symbol.substring(0, symbol.length() - 4) + "PERP"
                    : symbol + "USDT";
        }

        switch (commandParts[0]) {
            // place an order
            case "!o": // !o [symbol] [sl] [tp] [risk $] -l [price] -t [trigger]
                // market order
                if (commandParts.length == 5 && !commandParts[2].contains("-")) {
                    orderService.openMarketOrder(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3],
                            commandParts[4]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Market order opened successfully").reset());

                // market order without tp
                } else if (commandParts.length == 4 && !commandParts[2].contains("-")) {
                    orderService.openMarketOrder(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Market order opened successfully").reset());

                // limit order
                } else if (commandParts.length == 7 && !commandParts[2].contains("-")
                        && commandParts[5].equals("-l")) {
                    orderService.placeLimitOrder(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3],
                            commandParts[4],
                            commandParts[6]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Limit order placed successfully").reset());

                // limit order without tp
                } else if (commandParts.length == 6 && !commandParts[2].contains("-")
                        && commandParts[4].equals("-l")){
                    orderService.placeLimitOrder(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3],
                            commandParts[5]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Limit order placed successfully").reset());

                // conditional market order
                } else if (commandParts.length == 7 && commandParts[5].equals("-t")) {
                    orderService.placeConditionalMarketOrder(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3],
                            commandParts[4],
                            commandParts[6]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Conditional market order placed successfully")
                            .reset());

                // conditional market order without tp
                } else if (commandParts.length == 6 && commandParts[4].equals("-t")){
                    orderService.placeConditionalMarketOrder(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3],
                            commandParts[5]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Conditional market order placed successfully")
                            .reset());

                // conditional limit order
                } else if (commandParts.length == 9 && commandParts[5].equals("-l") && commandParts[7].equals("-t")) {
                    orderService.placeConditionalLimitOrder(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3],
                            commandParts[4],
                            commandParts[6],
                            commandParts[8]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Conditional limit order placed successfully")
                            .reset());

                // conditional limit order without tp
                } else if (commandParts.length == 8 && commandParts[4].equals("-l") && commandParts[6].equals("-t")){
                    orderService.placeConditionalLimitOrder(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3],
                            commandParts[5],
                            commandParts[7]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Conditional limit order placed successfully")
                            .reset());


                // !o [symbol] -buy/sell [quantity] -l [price]
                // market order by quantity
                } else if (commandParts.length == 4 && commandParts[2].contains("-")){
                    orderService.openMarketOrderByQuantity(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Market order opened successfully").reset());

                // limit order by quantity
                } else if (commandParts.length == 6 && commandParts[2].contains("-")){
                    orderService.placeLimitOrderByQuantity(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3],
                            commandParts[5]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Limit order placed successfully").reset());

                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // close positions
            case "!x": // !x [symbol] [percent]
                if (commandParts.length == 3 || commandParts.length == 2 || commandParts.length == 1) {
                    if (commandParts.length == 3) { // percent
                        orderService.closePositions(
                                commandParts[1],
                                commandParts[2]
                        );
                    } else if (commandParts.length == 2) { // 100%
                        orderService.closePositions(
                                commandParts[1],
                               "100"
                        );
                    } else { // close all
                        orderService.closePositions("-all", "100");
                    }
                    System.out.println(ansi().fgBrightGreen().a("  Positions closed successfully").reset());
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // cancel limit orders
            case "!cl": // !cl [symbol]
                if (commandParts.length == 2) { // by symbol
                    orderService.cancelLimitOrders(
                            commandParts[1]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Limit orders cancelled successfully").reset());
                } else if (commandParts.length == 1) { // all limit orders
                    orderService.cancelLimitOrders(
                            "-all"
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Limit orders cancelled successfully").reset());
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // cancel conditional orders
            case "!cc": // !cc [symbol]
                if (commandParts.length == 2) { // by symbol
                    orderService.cancelStopOrders(
                            commandParts[1]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Conditional orders cancelled successfully")
                            .reset());
                } else if (commandParts.length == 1) { // all orders
                    orderService.cancelStopOrders(
                            "-all"
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Conditional orders cancelled successfully")
                            .reset());
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // manage stop-loss
            case "!sl": // !sl [symbol] [price]
                if (commandParts.length == 3) { // full SL
                    orderService.manageStopLoss(
                            commandParts[1],
                            commandParts[2]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Stop-loss updated successfully").reset());
                } else if (commandParts.length == 4) { // partial SL
                    orderService.manageStopLoss(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Stop-loss updated successfully").reset());
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // manage take-profit
            case "!tp": // !tp [symbol] [price] [percent]
                if (commandParts.length == 3) { // full TP
                    orderService.manageTakeProfit(
                            commandParts[1],
                            commandParts[2]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Take-profit updated successfully").reset());
                } else if (commandParts.length == 4) { // partial TP
                    orderService.manageTakeProfit(
                            commandParts[1],
                            commandParts[2],
                            commandParts[3]
                    );
                    System.out.println(ansi().fgBrightGreen().a("  Take-profit updated successfully").reset());
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // set the leverage for specified pair
            case "!lev": // !lev [symbol] [leverage]
                if (commandParts.length == 3 || commandParts.length == 2) {
                    if (commandParts.length == 3) { // lev
                        orderService.setLeverage(
                                commandParts[1],
                                commandParts[2]
                        );
                    } else { // max lev
                        orderService.setLeverage(
                                commandParts[1],
                                "-max"
                        );
                    }
                    System.out.println(ansi().fgBrightGreen().a("  Leverage set successfully").reset());
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // get position info
            case "!gp": // !gp [symbol]
                if (commandParts.length == 2 || commandParts.length == 1) {
                    List<Position> positions;
                    if (commandParts.length == 2) { // position by symbol
                        positions = orderService.positions(commandParts[1]);
                    } else { // all positions
                        positions = orderService.positions("-all");
                    }
                    // print positions
                    for (int i = 0; i < positions.size(); i++) {
                        String positionStr = positions.get(i).toString();
                        if (i > 0) {
                            positionStr = positionStr.substring(positionStr.indexOf('\n') + 1);
                        }
                        System.out.println(positionStr);
                    }
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // get placed limit orders
            case "!gl": // !gl [symbol]
                if (commandParts.length == 2 || commandParts.length == 1) {
                    List<LimitOrder> limitOrders;
                    if (commandParts.length == 2) { // limit orders by symbol
                        limitOrders = orderService.limitOrders(commandParts[1]);
                    } else { // all limit orders
                        limitOrders = orderService.limitOrders("-all");
                    }
                    // print limit orders
                    for (int i = 0; i < limitOrders.size(); i++) {
                        String orderStr = limitOrders.get(i).toString();
                        if (i > 0) {
                            orderStr = orderStr.substring(orderStr.indexOf('\n') + 1);
                        }
                        System.out.println(orderStr);
                    }
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // get placed conditional orders
            case "!gc": // !gc [symbol]
                if (commandParts.length == 2 || commandParts.length == 1) {
                    List<ConditionalOrder> conditionalOrders;
                    if (commandParts.length == 2) { // conditional orders by symbol
                        conditionalOrders = orderService.conditionalOrders(commandParts[1]);
                    } else { // all conditional orders
                        conditionalOrders = orderService.conditionalOrders("-all");
                    }
                    // print conditional orders
                    for (int i = 0; i < conditionalOrders.size(); i++) {
                        String orderStr = conditionalOrders.get(i).toString();
                        if (i > 0) {
                            orderStr = orderStr.substring(orderStr.indexOf('\n') + 1);
                        }
                        System.out.println(orderStr);
                    }
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // get wallet balance
            case "!bal":
                if (commandParts.length == 1) {
                    WalletBalance walletBalance = orderService.getWalletBalance();
                    System.out.println(walletBalance);
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // test API request
            case "!ping":
                if (commandParts.length == 1) {
                    orderService.testRequest();
                    System.out.println(ansi().fgBrightGreen()
                            .a("  Connection OK, API credentials are valid").reset());
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // clear the terminal screen
            case "!clear":
                if (commandParts.length == 1) {
                    System.out.print("\u001b[2J\u001b[H");
                    System.out.flush();
                    printHeader();
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // display help menu
            case "!help":
                if (commandParts.length == 1) {
                    StringBuilder help = new StringBuilder();

                    help.append("\n");
                    help.append("  !o [symbol]* [sl]* [tp] [risk]* -l [price] -t [trigger]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  open order by risk").reset()).append("\n");
                    help.append("\n");
                    help.append("  !o [symbol]* -buy/sell [qty]* -l [price]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  open order by quantity").reset()).append("\n");
                    help.append("\n");
                    help.append("  !x [symbol] [percent]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  close position").reset()).append("\n");
                    help.append("\n");
                    help.append("  !cl [symbol]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  cancel limit orders").reset()).append("\n");
                    help.append("\n");
                    help.append("  !cc [symbol]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  cancel conditional orders").reset()).append("\n");
                    help.append("\n");
                    help.append("  !sl [symbol]* [price]* [percent]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  manage stop-loss").reset()).append("\n");
                    help.append("\n");
                    help.append("  !tp [symbol]* [price]* [percent]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  manage take-profit").reset()).append("\n");
                    help.append("\n");
                    help.append("  !lev [symbol]* [leverage]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  set leverage").reset()).append("\n");
                    help.append("\n");
                    help.append("  !gp [symbol]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  get position info").reset()).append("\n");
                    help.append("\n");
                    help.append("  !gl [symbol]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  get limit orders").reset()).append("\n");
                    help.append("\n");
                    help.append("  !gc [symbol]\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  get conditional orders").reset()).append("\n");
                    help.append("\n");
                    help.append("  !bal\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  get wallet balance").reset()).append("\n");
                    help.append("\n");
                    help.append("  !ping\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  send test API request").reset()).append("\n");
                    help.append("\n");
                    help.append("  !clear\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  clear terminal").reset()).append("\n");
                    help.append("\n");
                    help.append("  !help\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  display help menu").reset()).append("\n");
                    help.append("\n");
                    help.append("  !exit\n");
                    help.append(ansi().a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT).a("  exit program").reset()).append("\n");
                    help.append("\n");
                    help.append("  * - required parameter\n");
                    help.append("\n");
                    help.append("  Docs: https://heinmer.github.io/cli-order-executor\n");
                    help.append("  Repo: https://github.com/heinmer/cli-order-executor\n");
                    System.out.println(help);
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // stop the program
            case "!exit":
                if (commandParts.length == 1) {
                    System.exit(0);
                } else {
                    throw new InvalidCommandException("Incorrect command format, try again");
                }
                break;

            // invalid command
            default:
                throw new InvalidCommandException("Unknown command, try again");
        }
    }

}
