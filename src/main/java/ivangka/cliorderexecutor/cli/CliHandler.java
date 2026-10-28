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
        String version = "v1.19.11";
        String indent = "  ";
        String[] bars = {
                "    █ ▄ █     ▄ █ ▄       ▄ █ █ ▄",
                "▄ █ █ █ █ █ ▄ █ █ █ █ ▄ █ █ █ █ █ ▄"
        };
        int width = bars[1].length();
        int titlePadding = Math.max(1, width - title.length() - version.length());

        System.out.println();
        for (String bar : bars) {
            System.out.println(indent + bar);
        }
        System.out.println(indent + title + " ".repeat(titlePadding) + version);
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
            case "!help": // !help [command]
                if (commandParts.length == 1) { // all commands
                    StringBuilder help = new StringBuilder();

                    help.append("\n");
                    help.append("  Type `!help [command]` to see details about a specific command\n");
                    help.append("  (e.g. `!help o` or `!help !o`)\n");
                    help.append("\n");
                    help.append("  !o          open order\n");
                    help.append("\n");
                    help.append("  !x          close position\n");
                    help.append("  !cl         cancel limit orders\n");
                    help.append("  !cc         cancel conditional orders\n");
                    help.append("\n");
                    help.append("  !sl         manage stop-loss\n");
                    help.append("  !tp         manage take-profit\n");
                    help.append("\n");
                    help.append("  !lev        set leverage\n");
                    help.append("\n");
                    help.append("  !gp         get position info\n");
                    help.append("  !gl         get limit orders\n");
                    help.append("  !gc         get conditional orders\n");
                    help.append("\n");
                    help.append("  !bal        get wallet balance\n");
                    help.append("\n");
                    help.append("  !ping       send test API request\n");
                    help.append("  !clear      clear terminal\n");
                    help.append("  !help       display help menu\n");
                    help.append("  !exit       exit program\n");
                    help.append("\n");
                    help.append("  Docs: https://heinmer.github.io/cli-order-executor\n");
                    help.append("  Repo: https://github.com/heinmer/cli-order-executor\n");
                    System.out.println(help);

                } else if (commandParts.length == 2) { // specific command
                    // revert symbol formatting applied above (e.g. "SLUSDT" -> "sl")
                    String helpCommand = commandParts[1].toLowerCase();
                    if (helpCommand.endsWith("usdt")) {
                        helpCommand = helpCommand.substring(0, helpCommand.length() - 4);
                    }
                    if (helpCommand.startsWith("!")) {
                        helpCommand = helpCommand.substring(1);
                    }

                    StringBuilder help = new StringBuilder();
                    help.append("\n");

                    switch (helpCommand) {
                        case "o":
                            help.append("  I.\n");
                            help.append("\n");
                            help.append("  !o [symbol]* [sl]* [tp] [risk]* -l [price] -t [trigger]\n");
                            help.append("  * -required parameter\n");
                            help.append("\n");
                            help.append("  Opens a perpetual futures order with exact order sizing based on a specified\n");
                            help.append("  risk amount, while factoring in trading fees. The leverage used will be the\n");
                            help.append("  default setting for the given trading pair (symbol).\n");
                            help.append("\n");
                            help.append("  Important: slippage is not considered in the order size calculation.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol*          -trading pair\n");
                            help.append("  - sl*              -stop-loss price\n");
                            help.append("  - tp               -take-profit price\n");
                            help.append("  - risk*            -amount in USD to risk on the trade\n");
                            help.append("  - -l [price]       -limit order price\n");
                            help.append("  - -t [trigger]     -trigger price\n");
                            help.append("\n");
                            help.append("  II.\n");
                            help.append("\n");
                            help.append("  !o [symbol]* -buy/sell [qty]* -l [price]\n");
                            help.append("  * -required parameter\n");
                            help.append("\n");
                            help.append("  Opens a perpetual futures order based on a specific quantity of contracts.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol*          -trading pair\n");
                            help.append("  - -buy/sell [qty]* -side and quantity of contracts\n");
                            help.append("  - -l [price]       -limit order price\n");
                            break;

                        case "x":
                            help.append("  !x [symbol] [percent]\n");
                            help.append("\n");
                            help.append("  Closes an open position for the given trading pair or for all pairs.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol  -trading pair\n");
                            help.append("  - percent -percentage of position to close\n");
                            break;

                        case "cl":
                            help.append("  !cl [symbol]\n");
                            help.append("\n");
                            help.append("  Cancels all limit orders for specified trading pair or for all pairs.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol -trading pair\n");
                            break;

                        case "cc":
                            help.append("  !cc [symbol]\n");
                            help.append("\n");
                            help.append("  Cancels all open conditional orders (including stop-loss and take-profit orders)\n");
                            help.append("  for specified trading pair or for all pairs.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol -trading pair\n");
                            break;

                        case "sl":
                            help.append("  !sl [symbol]* [price]* [percent]\n");
                            help.append("  * -required parameter\n");
                            help.append("\n");
                            help.append("  Updates, sets, or removes a stop-loss order for an existing position.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol*  -trading pair\n");
                            help.append("  - price*   -stop-loss price (\"0\" to remove a full position SL order)\n");
                            help.append("  - percent  -percentage of the existing position (for a partial position SL order)\n");
                            break;

                        case "tp":
                            help.append("  !tp [symbol]* [price]* [percent]\n");
                            help.append("  * -required parameter\n");
                            help.append("\n");
                            help.append("  Updates, sets, or removes a take-profit order for an existing position.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol*  -trading pair\n");
                            help.append("  - price*   -take-profit price (\"0\" to remove a full position TP order)\n");
                            help.append("  - percent  -percentage of the existing position (for a partial position TP order)\n");
                            break;

                        case "lev":
                            help.append("  !lev [symbol]* [leverage]\n");
                            help.append("  * -required parameter\n");
                            help.append("\n");
                            help.append("  Sets leverage for the trading pair. This applies to both existing positions\n");
                            help.append("  and any new orders for this symbol.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol*  -trading pair\n");
                            help.append("  - leverage -leverage size\n");
                            break;

                        case "gp":
                            help.append("  !gp [symbol]\n");
                            help.append("\n");
                            help.append("  Displays information about the current positions.\n");
                            help.append("\n");
                            help.append("  It shows symbol name, side, size, average entry price, leverage, liquidation price,\n");
                            help.append("  full position stop-loss and take-profit prices.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol -trading pair\n");
                            break;

                        case "gl":
                            help.append("  !gl [symbol]\n");
                            help.append("\n");
                            help.append("  Displays information about active limit orders.\n");
                            help.append("\n");
                            help.append("  It shows symbol name, price, quantity, side, order type, full position stop-loss\n");
                            help.append("  and take-profit prices.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol -trading pair\n");
                            break;

                        case "gc":
                            help.append("  !gc [symbol]\n");
                            help.append("\n");
                            help.append("  Displays information about active conditional orders.\n");
                            help.append("\n");
                            help.append("  It shows symbol name, trigger price, price, quantity, side, order type,\n");
                            help.append("  full position stop-loss and take-profit prices.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - symbol -trading pair\n");
                            break;

                        case "bal":
                            help.append("  !bal\n");
                            help.append("\n");
                            help.append("  Displays information about your unified trading account balance.\n");
                            help.append("\n");
                            help.append("  It shows:\n");
                            help.append("  - Wallet Balance    -overall wallet balance (all assets in the account)\n");
                            help.append("  - Margin Balance    -balance including unrealized PnL and accrued fees\n");
                            help.append("  - Available Balance -balance available for opening new positions or placing orders\n");
                            break;

                        case "ping":
                            help.append("  !ping\n");
                            help.append("\n");
                            help.append("  Sends a test request to the Bybit API to verify that the connection\n");
                            help.append("  and credentials are working.\n");
                            break;

                        case "clear":
                            help.append("  !clear\n");
                            help.append("\n");
                            help.append("  Clears the terminal screen.\n");
                            break;

                        case "help":
                            help.append("  !help [command]\n");
                            help.append("\n");
                            help.append("  Displays all available commands with links to the docs and repository,\n");
                            help.append("  or details about the specified command.\n");
                            help.append("\n");
                            help.append("  Parameters:\n");
                            help.append("  - command -command name (with or without \"!\")\n");
                            break;

                        case "exit":
                            help.append("  !exit\n");
                            help.append("\n");
                            help.append("  Stops the program.\n");
                            break;

                        default:
                            throw new InvalidCommandException("Unknown command, try again");
                    }

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
