import org.hyperskill.hstest.dynamic.input.DynamicTestingMethod;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.TestedProgram;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;

public class JsonDatabaseTest extends StageTest<String> {

    private static final String OK_STATUS = "OK";
    private static final String ERROR_STATUS = "ERROR";
    private static final String NO_SUCH_KEY_REASON = "No such key";
    private static final String WRONG_EXIT = "The server should stop when client sends 'exit' request";

    @DynamicTestingMethod
    CheckResult checkExit() {

        TestedProgram server = getServer();
        server.startInBackground();

        TestedProgram client = getClient();
        client.start("-t", "exit");

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!server.isFinished()) {
            server.stop();
            return CheckResult.wrong(WRONG_EXIT);
        }

        return CheckResult.correct();
    }

    @DynamicTestingMethod
    CheckResult testInputs() {

        TestedProgram server = getServer();
        server.startInBackground();

        TestedProgram client;
        String output;
        String expectedValue;

        client = getClient();
        output = client.start("-t", "get", "-k", "1");

        String requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "1")
            );
        String responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "set", "-k", "1", "-v", "Hello world!");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "set")
                .value("key", "1")
                .value("value", "Hello world!")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "set", "-k", "1", "-v", "HelloWorld!");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "set")
                .value("key", "1")
                .value("value", "HelloWorld!")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "get", "-k", "1");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "1")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
                .value("value", "HelloWorld!")
            );


        client = getClient();
        output = client.start("-t", "delete", "-k", "1");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "1")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "delete", "-k", "1");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "1")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "get", "-k", "1");
        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "1")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "set", "-k", "text", "-v", "Some text here");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "set")
                .value("key", "text")
                .value("value", "Some text here")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "get", "-k", "text");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "text")
            );
        expectedValue = "Some text here";
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
                .value("value", expectedValue)
            );


        client = getClient();
        output = client.start("-t", "get", "-k", "56");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "56")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "delete", "-k", "56");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "56")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "delete", "-k", "100");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "100")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );

        client = getClient();
        output = client.start("-t", "delete", "-k", "That key doesn't exist");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "That key doesn't exist")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        client.start("-t", "exit");

        return CheckResult.correct();
    }

    private static TestedProgram getClient() {
        return new TestedProgram("client");
    }

    private static TestedProgram getServer() {
        return new TestedProgram("server");
    }

}
