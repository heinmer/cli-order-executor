package ivangka.cliorderexecutor.service;

import ivangka.cliorderexecutor.exception.*;
import ivangka.cliorderexecutor.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class OrderService {

    private final ApiService apiService;
    private final RiskCalculatorService riskCalculatorService;

    @Autowired
    public OrderService(RiskCalculatorService riskCalculatorService, ApiService apiService) {
        this.riskCalculatorService = riskCalculatorService;
        this.apiService = apiService;
    }

    // open a market order
    public void openMarketOrder(String symbol, String stopLoss, String takeProfit, String risk)
            throws BadRetCodeException, InvalidCommandException {

        Ticker ticker = apiService.ticker(symbol);
        Instrument instrument = apiService.instrumentInfo(symbol);
        String orderSize = orderSize(risk, ticker.getLastPrice(), stopLoss, instrument.getQtyStep(), "Market");

        BigDecimal stopLossBD = new BigDecimal(stopLoss);
        BigDecimal priceBD = new BigDecimal(ticker.getLastPrice());
        String side = stopLossBD.compareTo(priceBD) < 0 ? "Buy" : "Sell";

        apiService.createMarketOrder(symbol, side, orderSize, stopLoss, takeProfit);
    }

    // open a market order without tp
    public void openMarketOrder(String symbol, String stopLoss, String risk)
            throws BadRetCodeException, InvalidCommandException {

        Ticker ticker = apiService.ticker(symbol);
        Instrument instrument = apiService.instrumentInfo(symbol);
        String orderSize = orderSize(risk, ticker.getLastPrice(), stopLoss, instrument.getQtyStep(), "Market");

        BigDecimal stopLossBD = new BigDecimal(stopLoss);
        BigDecimal priceBD = new BigDecimal(ticker.getLastPrice());
        String side = stopLossBD.compareTo(priceBD) < 0 ? "Buy" : "Sell";

        apiService.createMarketOrder(symbol, side, orderSize, stopLoss);
    }

    // place a limit order
    public void placeLimitOrder(String symbol, String stopLoss, String takeProfit, String risk, String price)
            throws BadRetCodeException, InvalidCommandException {

        Instrument instrument = apiService.instrumentInfo(symbol);
        String orderSize = orderSize(risk, price, stopLoss, instrument.getQtyStep(), "Limit");

        BigDecimal stopLossBD = new BigDecimal(stopLoss);
        BigDecimal priceBD = new BigDecimal(price);
        String side = stopLossBD.compareTo(priceBD) < 0 ? "Buy" : "Sell";

        apiService.createLimitOrder(symbol, side, orderSize, price, stopLoss, takeProfit);
    }

    // place limit order without tp
    public void placeLimitOrder(String symbol, String stopLoss, String risk, String price)
            throws BadRetCodeException, InvalidCommandException {

        Instrument instrument = apiService.instrumentInfo(symbol);
        String orderSize = orderSize(risk, price, stopLoss, instrument.getQtyStep(), "Limit");

        BigDecimal stopLossBD = new BigDecimal(stopLoss);
        BigDecimal priceBD = new BigDecimal(price);
        String side = stopLossBD.compareTo(priceBD) < 0 ? "Buy" : "Sell";

        apiService.createLimitOrder(symbol, side, orderSize, price, stopLoss);
    }

    // place a conditional market order
    public void placeConditionalMarketOrder(String symbol, String stopLoss, String takeProfit, String risk,
                                     String triggerPrice) throws BadRetCodeException, InvalidCommandException {

        Ticker ticker = apiService.ticker(symbol);
        Instrument instrument = apiService.instrumentInfo(symbol);
        String orderSize = orderSize(risk, triggerPrice, stopLoss, instrument.getQtyStep(), "Market");

        BigDecimal stopLossBD = new BigDecimal(stopLoss);
        BigDecimal triggerPriceBD = new BigDecimal(triggerPrice);
        String side = stopLossBD.compareTo(triggerPriceBD) < 0 ? "Buy" : "Sell";
        BigDecimal priceBD = new BigDecimal(ticker.getLastPrice());
        int triggerDirection = priceBD.compareTo(triggerPriceBD) < 0 ? 1 : 2;

        apiService.createConditionalMarketOrder(symbol, side, orderSize, stopLoss, takeProfit, triggerDirection, triggerPrice);
    }

    // place a conditional market order without tp
    public void placeConditionalMarketOrder(String symbol, String stopLoss, String risk,
                                     String triggerPrice) throws BadRetCodeException, InvalidCommandException {

        Ticker ticker = apiService.ticker(symbol);
        Instrument instrument = apiService.instrumentInfo(symbol);
        String orderSize = orderSize(risk, triggerPrice, stopLoss, instrument.getQtyStep(), "Market");

        BigDecimal stopLossBD = new BigDecimal(stopLoss);
        BigDecimal triggerPriceBD = new BigDecimal(triggerPrice);
        String side = stopLossBD.compareTo(triggerPriceBD) < 0 ? "Buy" : "Sell";
        BigDecimal priceBD = new BigDecimal(ticker.getLastPrice());
        int triggerDirection = priceBD.compareTo(triggerPriceBD) < 0 ? 1 : 2;

        apiService.createConditionalMarketOrder(symbol, side, orderSize, stopLoss, triggerDirection, triggerPrice);
    }

    // place a conditional limit order
    public void placeConditionalLimitOrder(String symbol, String stopLoss, String takeProfit, String risk, String price,
                                    String triggerPrice) throws BadRetCodeException, InvalidCommandException {

        Ticker ticker = apiService.ticker(symbol);
        Instrument instrument = apiService.instrumentInfo(symbol);
        String orderSize = orderSize(risk, price, stopLoss, instrument.getQtyStep(), "Limit");

        BigDecimal stopLossBD = new BigDecimal(stopLoss);
        BigDecimal priceBD = new BigDecimal(price);
        BigDecimal triggerPriceBD = new BigDecimal(triggerPrice);
        String side = stopLossBD.compareTo(priceBD) < 0 ? "Buy" : "Sell";
        BigDecimal currentPriceBD = new BigDecimal(ticker.getLastPrice());
        int triggerDirection = currentPriceBD.compareTo(triggerPriceBD) < 0 ? 1 : 2;

        apiService.createConditionalLimitOrder(symbol, side, orderSize, price, stopLoss, takeProfit, triggerDirection,
                triggerPrice);
    }

    // place a conditional limit order without tp
    public void placeConditionalLimitOrder(String symbol, String stopLoss, String risk, String price,
                                    String triggerPrice) throws BadRetCodeException, InvalidCommandException {

        Ticker ticker = apiService.ticker(symbol);
        Instrument instrument = apiService.instrumentInfo(symbol);
        String orderSize = orderSize(risk, price, stopLoss, instrument.getQtyStep(), "Limit");

        BigDecimal stopLossBD = new BigDecimal(stopLoss);
        BigDecimal priceBD = new BigDecimal(price);
        BigDecimal triggerPriceBD = new BigDecimal(triggerPrice);
        String side = stopLossBD.compareTo(priceBD) < 0 ? "Buy" : "Sell";
        BigDecimal currentPriceBD = new BigDecimal(ticker.getLastPrice());
        int triggerDirection = currentPriceBD.compareTo(triggerPriceBD) < 0 ? 1 : 2;

        apiService.createConditionalLimitOrder(symbol, side, orderSize, price, stopLoss, triggerDirection, triggerPrice);
    }

    // open a market order by quantity
    public void openMarketOrderByQuantity(String symbol, String side, String orderSize)
            throws BadRetCodeException, InvalidCommandException {

        if (side.equals("-buy")) {
            side = "Buy";
        } else if (side.equals("-sell")) {
            side = "Sell";
        } else {
            throw new InvalidCommandException("Incorrect command format. Type `!help [command]` to see the correct usage");
        }
        apiService.createMarketOrderByQuantity(symbol, side, orderSize);
    }

    // place a limit order by quantity
    public void placeLimitOrderByQuantity(String symbol, String side, String orderSize, String price)
            throws BadRetCodeException, InvalidCommandException {

        if (side.equals("-buy")) {
            side = "Buy";
        } else if (side.equals("-sell")) {
            side = "Sell";
        } else {
            throw new InvalidCommandException("Incorrect command format. Type `!help [command]` to see the correct usage");
        }
        apiService.createLimitOrderByQuantity(symbol, side, orderSize, price);
    }

    // close positions
    public void closePositions(String symbol, String percent)
            throws BadRetCodeException, InvalidCommandException, TooSmallOrderSizeException, InterruptedException,
            OrderNotFoundException {

        // get positions
        List<Position> positions;
        if (symbol.equals("-all")) {
            positions = apiService.positions(); // 2 req (Position)
        } else {
            positions = apiService.positions(symbol); // 1 req (Position)
        }
        if (positions.isEmpty() || positions.get(0).getSize().equals("0")) {
            throw new OrderNotFoundException("Positions were not found");
        }

        // checking user's percent
        BigDecimal percentBD;
        try {
            percentBD = new BigDecimal(percent);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Incorrect command format. Type `!help [command]` to see the correct usage");
        }
        // 0 < percent <= 100
        if (percentBD.compareTo(BigDecimal.ZERO) <= 0 || percentBD.compareTo(new BigDecimal("100")) > 0) {
            throw new InvalidCommandException("Percent value is incorrect");
        }

        for (Position position : positions) {
            String side = position.getSide().equals("Buy") ? "Sell" : "Buy";

            // if percent == 100 (full position)
            if (percentBD.compareTo(new BigDecimal("100")) == 0) {
                apiService.closePosition(position.getSymbol(), side, position.getSize()); // 1 req (Trade)
                Thread.sleep(150);
                continue;
            }

            // get min order size and step of the symbol
            Instrument instrument = apiService.instrumentInfo(position.getSymbol()); // 1 req (Market)
            BigDecimal minOrderSizeBD = new BigDecimal(instrument.getMinOrderQty());
            BigDecimal stepBD = new BigDecimal(instrument.getQtyStep());

            // calculating final size
            BigDecimal orderSizeBD = new BigDecimal(position.getSize());
            BigDecimal percentFactor = percentBD.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
            BigDecimal sizeBD = orderSizeBD.multiply(percentFactor);
            // formatting size by price step
            BigDecimal roundedSizeBD = sizeBD.divide(stepBD, 0, RoundingMode.DOWN).multiply(stepBD)
                    .stripTrailingZeros();

            // size >= minOrderSize
            if (percentBD.compareTo(BigDecimal.valueOf(100)) != 0 && roundedSizeBD.compareTo(minOrderSizeBD) < 0) {
                throw new TooSmallOrderSizeException("Position size to close is too small");
            }

            apiService.closePosition(position.getSymbol(), side, roundedSizeBD.toPlainString()); // 1 req (Trade)
            Thread.sleep(150);
        }
    }

    // cancel orders
    public void cancelLimitOrders(String symbol) throws BadRetCodeException {
        if (symbol.equals("-all")) {
            apiService.cancelLimitOrders();
        } else {
            apiService.cancelLimitOrders(symbol);
        }
    }

    // cancel conditional orders
    public void cancelStopOrders(String symbol) throws BadRetCodeException {
        if (symbol.equals("-all")) {
            apiService.cancelConditionalOrders();
        } else {
            apiService.cancelConditionalOrders(symbol);
        }
    }

    // manage full stop-loss
    public void manageStopLoss(String symbol, String price) throws BadRetCodeException {
        apiService.manageStopLoss(symbol, price);
    }

    // manage partial stop-loss
    public void manageStopLoss(String symbol, String price, String percent)
            throws BadRetCodeException, OrderNotFoundException, InvalidCommandException, TooSmallOrderSizeException {

        // get the position
        List<Position> positions = apiService.positions(symbol); // 1 req
        if (positions.isEmpty() || positions.get(0).getSize().equals("0")) {
            throw new OrderNotFoundException("Positions were not found");
        }
        Position position = positions.get(0);

        // checking percent
        BigDecimal percentBD;
        try {
            percentBD = new BigDecimal(percent);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Incorrect command format. Type `!help [command]` to see the correct usage");
        }
        // 0 < percent <= 100
        if (percentBD.compareTo(BigDecimal.ZERO) <= 0 || percentBD.compareTo(new BigDecimal("100")) > 0) {
            throw new InvalidCommandException("Percent value is incorrect");
        }

        // if percent == 100 (full SL)
        if (percentBD.compareTo(new BigDecimal("100")) == 0) {
            manageStopLoss(symbol, price);
            return;
        }

        // get min order size and step of the symbol
        Instrument instrument = apiService.instrumentInfo(position.getSymbol()); // 1 req
        BigDecimal minOrderSizeBD = new BigDecimal(instrument.getMinOrderQty());
        BigDecimal stepBD = new BigDecimal(instrument.getQtyStep());

        // calculating final size
        BigDecimal orderSizeBD = new BigDecimal(position.getSize());
        BigDecimal percentFactor = percentBD.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        BigDecimal sizeBD = orderSizeBD.multiply(percentFactor);
        // formatting size by price step
        BigDecimal roundedSizeBD = sizeBD.divide(stepBD, 0, RoundingMode.DOWN).multiply(stepBD)
                .stripTrailingZeros();

        // size >= minOrderSize
        if (percentBD.compareTo(BigDecimal.valueOf(100)) != 0 && roundedSizeBD.compareTo(minOrderSizeBD) < 0) {
            throw new TooSmallOrderSizeException("Stop-loss size is too small");
        }

        apiService.manageStopLoss(position.getSymbol(), price, roundedSizeBD.toPlainString()); // 1 req
    }

    // manage full take-profit
    public void manageTakeProfit(String symbol, String price) throws BadRetCodeException {
        apiService.manageTakeProfit(symbol, price);
    }

    // manage partial take-profit
    public void manageTakeProfit(String symbol, String price, String percent)
            throws BadRetCodeException, OrderNotFoundException, InvalidCommandException, TooSmallOrderSizeException {

        // get the position
        List<Position> positions = apiService.positions(symbol); // 1 req
        if (positions.isEmpty() || positions.get(0).getSize().equals("0")) {
            throw new OrderNotFoundException("Positions were not found");
        }
        Position position = positions.get(0);

        // checking percent
        BigDecimal percentBD;
        try {
            percentBD = new BigDecimal(percent);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Incorrect command format. Type `!help [command]` to see the correct usage");
        }
        // 0 < percent <= 100
        if (percentBD.compareTo(BigDecimal.ZERO) <= 0 || percentBD.compareTo(new BigDecimal("100")) > 0) {
            throw new InvalidCommandException("Percent value is incorrect");
        }

        // if percent == 100 (full TP)
        if (percentBD.compareTo(new BigDecimal("100")) == 0) {
            manageTakeProfit(symbol, price);
            return;
        }

        // get min order size and step of the symbol
        Instrument instrument = apiService.instrumentInfo(position.getSymbol()); // 1 req
        BigDecimal minOrderSizeBD = new BigDecimal(instrument.getMinOrderQty());
        BigDecimal stepBD = new BigDecimal(instrument.getQtyStep());

        // calculating final size
        BigDecimal orderSizeBD = new BigDecimal(position.getSize());
        BigDecimal percentFactor = percentBD.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        BigDecimal sizeBD = orderSizeBD.multiply(percentFactor);
        // formatting size by price step
        BigDecimal roundedSizeBD = sizeBD.divide(stepBD, 0, RoundingMode.DOWN).multiply(stepBD)
                .stripTrailingZeros();

        // size >= minOrderSize
        if (percentBD.compareTo(BigDecimal.valueOf(100)) != 0 && roundedSizeBD.compareTo(minOrderSizeBD) < 0) {
            throw new TooSmallOrderSizeException("Take-profit size is too small");
        }

        apiService.manageTakeProfit(position.getSymbol(), price, roundedSizeBD.toPlainString()); // 1 req
    }

    // set the leverage for the trading pair
    public void setLeverage(String symbol, String leverage) throws BadRetCodeException {
        if (leverage.equals("-max")) {
            RiskLimit riskLimit = apiService.riskLimit(symbol);
            leverage = riskLimit.getMaxLeverage();
        }
        apiService.setLeverage(symbol, leverage);
    }

    // get positions info
    public List<Position> positions(String symbol) throws BadRetCodeException, OrderNotFoundException {
        List<Position> positions;
        if (symbol.equals("-all")) {
            positions = apiService.positions();
        } else {
            positions = apiService.positions(symbol);
        }
        if (positions.isEmpty() || positions.get(0).getSize().equals("0")) {
            throw new OrderNotFoundException("Positions were not found");
        }
        return positions;
    }

    // get placed limit orders
    public List<LimitOrder> limitOrders(String symbol) throws BadRetCodeException, OrderNotFoundException {
        List<LimitOrder> limitOrders;
        if (symbol.equals("-all")) {
            limitOrders = apiService.limitOrders();
        } else { // by symbol
            limitOrders = apiService.limitOrders(symbol);
        }
        if (limitOrders.isEmpty()) {
            throw new OrderNotFoundException("Limit orders were not found");
        }
        return limitOrders;
    }

    // get placed conditional orders
    public List<ConditionalOrder> conditionalOrders(String symbol) throws BadRetCodeException, OrderNotFoundException {
        List<ConditionalOrder> conditionalOrders;
        if (symbol.equals("-all")) {
            conditionalOrders = apiService.conditionalOrders();
        } else { // by symbol
            conditionalOrders = apiService.conditionalOrders(symbol);
        }
        if (conditionalOrders.isEmpty()) {
            throw new OrderNotFoundException("Conditional orders were not found");
        }
        return conditionalOrders;
    }

    // get wallet balance
    public WalletBalance getWalletBalance() throws BadRetCodeException {
        return apiService.walletBalance();
    }

    // test API request
    public void testRequest() throws BadRetCodeException {
        apiService.testRequest();
    }

    // get order size
    private String orderSize(String risk, String price, String stopLoss, String step, String orderType)
            throws InvalidCommandException {

        BigDecimal riskBD, priceBD, stopLossBD, stepBD;
        try {
            riskBD = new BigDecimal(risk);
            priceBD = new BigDecimal(price);
            stopLossBD = new BigDecimal(stopLoss);
            stepBD = new BigDecimal(step);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Incorrect command format. Type `!help [command]` to see the correct usage");
        }
        return riskCalculatorService.calculateOrderSize(riskBD, priceBD, stopLossBD, stepBD, orderType);
    }

}
