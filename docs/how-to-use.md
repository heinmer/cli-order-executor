**Follow these steps to get started:**

1) Install `Java` and `Maven`.

2) Download or clone this repository:

```
git clone https://github.com/heinmer/cli-order-executor.git
```

3) Create a Bybit API Key.

4) Configure API credentials in `application.properties`:

```properties
# bybit API
api.bybit.key=<YOUR_BYBIT_API_KEY>
api.bybit.secret=<YOUR_BYBIT_API_SECRET>
```

5) Compile the application:

```
mvn clean package -DskipTests
```

6) Run the application:

```
java -jar "<PATH>/target/cli-order-executor-0.0.1-SNAPSHOT.jar"
```
