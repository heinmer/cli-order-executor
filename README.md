# CLI Order Executor (v1.19.9)

A console tool for managing perpetual futures orders on Bybit via API.

> **Usage Notes**
> 
> - For all functions of the program to work correctly, set **One-Way Mode** in your Bybit account.
> - The program doesn't use the **Post-Only** option. If you place a limit order at a price worse than the current market price, it will be executed immediately as a market order.
> - Both **USDT** and **USDC** perpetual contracts are supported.
> - When trading USDT‑based pairs, you can omit the “USDT” suffix.  
>   *For example, entering `BTC` will automatically be interpreted as `BTCUSDT`.*

**Table of contents:**

* [Disclaimer](#disclaimer)
* [Full Documentation](#full-documentation)
* [Risk Management](#risk-management)
* [Commands](#commands)
  * [Open order by risk](#open-order-by-risk-o)
  * [Open order by quantity](#open-order-by-quantity-o)
  * [Close position](#close-position-x)
  * [Cancel limit orders](#cancel-limit-orders-cl)
  * [Cancel conditional orders](#cancel-conditional-orders-cc)
  * [Manage stop-loss](#manage-stop-loss-sl)
  * [Manage take-profit](#manage-take-profit-tp)
  * [Set leverage](#set-leverage-lev)
  * [Get position info](#get-position-info-gp)
  * [Get limit orders](#get-limit-orders-gl)
  * [Get conditional orders](#get-conditional-orders-gc)
  * [Get wallet balance](#get-wallet-balance-bal)
  * [Send test API request](#send-test-api-request-ping)
  * [Clear terminal](#clear-terminal-clear)
  * [Display help menu](#display-help-menu-help)
  * [Exit program](#exit-program-exit)
* [How to Use?](#how-to-use)
* [Error 10002](#error-10002)
* [License](#license)

## Disclaimer

This software is provided "as is", without any warranties. Use it at your own risk. The author is not responsible for any losses resulting from the use of this software, including but not limited to monetary losses, trading logic errors, API failures, violations of exchange terms of service (e.g., account bans or suspensions), or any other unforeseen consequences.

This software is not intended to provide financial or investment advice. Any trading or investment decisions you make using this software are your own responsibility. Consult a qualified financial advisor if needed.

Make sure you fully understand how this software works and how futures markets operate before using it. It is strongly recommended to test it in a safe environment (such as a sandbox account or with demo funds).

## Full Documentation

See the [Full Documentation](https://heinmer.github.io/cli-order-executor) with usage examples.

## Risk Management

The primary approach used by this program requires setting a stop-loss for each trade and automatically calculates the total position size based on:

1. Your specified risk amount (in USD)
2. The distance between entry and stop-loss prices
3. Bybit's trading fees

With this approach, **leverage does not affect your risk** — your risk is fixed by the stop-loss distance and position size calculation. Leverage only affects the number of concurrent positions — the higher the leverage, the more positions you can open within your available margin.

## Commands

    !o          open order

    !x          close position
    !cl         cancel limit orders
    !cc         cancel conditional orders

    !sl         manage stop-loss
    !tp         manage take-profit

    !lev        set leverage

    !gp         get position info
    !gl         get limit orders
    !gc         get conditional orders

    !bal        get wallet balance

    !ping       send test API request
    !clear      clear terminal
    !help       display help menu
    !exit       exit program

---

### Open order by risk `!o`

```
!o [symbol]* [sl]* [tp] [risk]* -l [price] -t [trigger]

* — required parameter
```

Opens a perpetual futures order with exact order sizing based on a specified risk amount, while factoring in trading fees. The leverage used will be the default setting for the given trading pair (symbol).

> [!IMPORTANT]
> 
> Slippage is not considered in the order size calculation.

Parameters:

- `symbol*` — trading pair
- `sl*` — stop-loss price
- `tp` — take-profit price
- `risk*` — amount in USD to risk on the trade
- `-l [price]` — limit order price
- `-t [trigger]` — trigger price

---

### Open order by quantity `!o`

```
!o [symbol]* -buy/sell [qty]* -l [price]

* — required parameter
```

Opens a perpetual futures order based on a specific quantity of contracts.

Parameters:

- `symbol*` — trading pair
- `-buy/sell [qty]*` — side and quantity of contracts
- `-l [price]` — limit order price

---

### Close position `!x`

```
!x [symbol] [percent]
```

Closes an open position for the given trading pair or for all pairs.

Parameters:

- `symbol` — trading pair
- `percent` — percentage of position to close

---

### Cancel limit orders `!cl`

```
!cl [symbol]
```

Cancels all limit orders for specified trading pair or for all pairs.

Parameters:

- `symbol` — trading pair

---

### Cancel conditional orders `!cc`

```
!cc [symbol]
```

Cancels all open conditional orders (including stop-loss and take-profit orders) for specified trading pair or for all pairs.

Parameters:

- `symbol` — trading pair

---

### Manage stop-loss `!sl`

```
!sl [symbol]* [price]* [percent]

* — required parameter
```

Updates, sets, or removes a stop-loss order for an existing position.

Parameters:

- `symbol*` — trading pair
- `price*` — stop-loss price ("0" to remove a full position SL order)
- `percent` — percentage of the existing position (for a partial position SL order)

---

### Manage take-profit `!tp`

```
!tp [symbol]* [price]* [percent]

* — required parameter
```

Updates, sets, or removes a take-profit order for an existing position.

Parameters:

- `symbol*` — trading pair
- `price*` — take-profit price ("0" to remove a full position TP order)
- `percent` — percentage of the existing position (for a partial position TP order)

---

### Set leverage `!lev`

```
!lev [symbol]* [leverage]

* — required parameter
```

Sets leverage for the trading pair. This applies to both existing positions and any new orders for this symbol.

Parameters:

- `symbol*` — trading pair
- `leverage` — leverage size

---

### Get position info `!gp`

```
!gp [symbol]
```

Displays information about the current positions.

It shows symbol name, side, size, average entry price, leverage, liquidation price, full position stop-loss and take-profit prices.

Parameters:

- `symbol` — trading pair

---

### Get limit orders `!gl`

```
!gl [symbol]
```

Displays information about active limit orders.

It shows symbol name, price, quantity, side, order type, full position stop-loss and take-profit prices.

Parameters:

- `symbol` — trading pair

---

### Get conditional orders `!gc`

```
!gc [symbol]
```

Displays information about active conditional orders.

It shows symbol name, trigger price, price, quantity, side, order type, full position stop-loss and take-profit prices.

Parameters:

- `symbol` — trading pair

---

### Get wallet balance `!bal`

Displays information about your **unified trading** account balance.

It shows:

- **Wallet Balance** — overall wallet balance (all assets in the account).
- **Margin Balance** — balance including unrealized PnL and accrued fees.
- **Available Balance** — balance available for opening new positions or placing orders.

---

### Send test API request `!ping`

Sends a test request to the Bybit API to verify that the connection and credentials are working.

---

### Clear terminal `!clear`

Clears the terminal screen.

---

### Display help menu `!help`

Displays all available commands and provides links to the docs and repository.

---

### Exit program `!exit`

Stops the program.

---

## How to Use?

Follow these steps to get started.

1. Install `Java` and `Maven`.
2. Download or clone this repository:

```
git clone https://github.com/heinmer/cli-order-executor.git
```

3. Create a Bybit API Key.
4. Configure API credentials in `application.properties`:

```properties
# bybit API
api.bybit.key=<YOUR_BYBIT_API_KEY>
api.bybit.secret=<YOUR_BYBIT_API_SECRET>
```

5. Compile the application:

```
mvn clean package -DskipTests
```

6. Run the application:

```
java -jar "<PATH>/target/cli-order-executor-0.0.1-SNAPSHOT.jar"
```

Here we go.

> You only need to complete steps 1-5 once.

## Error 10002

If you get "Error Code 10002", it means your PC's clock is out of sync with the server's time.   
To fix this, you just need to synchronize the time in your PC settings.

*[See the full list of error codes.](https://bybit-exchange.github.io/docs/v5/error)*

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
