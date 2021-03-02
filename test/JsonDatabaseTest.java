import org.hyperskill.hstest.dynamic.input.DynamicTestingMethod;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.TestedProgram;

public class JsonDatabaseTest extends StageTest<String> {

    private static final String OK_STATUS = "OK";
    private static final String ERROR_STATUS = "ERROR";

    private static final String WRONG_EXIT = "The server should stop when client sends 'exit' request";
    private static final String WRONG_GET_EMPTY_CELL_WITH_ERROR = "When a client tries to get an empty cell from " +
            "the server, the server should response with 'ERROR' and the client should print that response";
    private static final String WRONG_SET_VALUE_TO_CELL_WITH_OK = "When a client tries to save a value on" +
            " the server, the server should save the value and response with '" + OK_STATUS + "'. The client should " +
            "print that response";
    private static final String WRONG_GET_VALUE = "When a client tries to get a not empty cell from the server, " +
            "the server should response with a value of the cell. The client should pint received value.\n" +
            "May be the problem is in processing 'set' action:\nIf the specified cell already contains " +
            "information, you should simply rewrite it.";
    private static final String WRONG_DELETE = "When a client tries to delete a value from the cell on the server," +
            " the server should assign an empty string to this cell and response with '" + OK_STATUS + "'.";
    private static final String WRONG_DELETE_EMPTY = "When a client tries to delete a cell with an empty value from the server," +
            " the server should assign an empty string to this cell and response with '" + OK_STATUS + "'.";
    private static final String WRONG_DELETE_INDEX_OUT_OF_BOUNDS = "When a user tries to delete a cell which index " +
            "is less than 0 or greater than 1000, the server should response with  '" + ERROR_STATUS + "'.";

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
        output = client.start("-t", "get", "-i", "1");
        if (!output.toUpperCase().contains(ERROR_STATUS)) {
            return CheckResult.wrong(WRONG_GET_EMPTY_CELL_WITH_ERROR);
        }

        client = getClient();
        output = client.start("-t", "set", "-i", "1", "-m", "Hello world!");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_SET_VALUE_TO_CELL_WITH_OK);
        }

        client = getClient();
        output = client.start("-t", "set", "-i", "1", "-m", "HelloWorld!");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_SET_VALUE_TO_CELL_WITH_OK);
        }

        client = getClient();
        output = client.start("-t", "get", "-i", "1");
        expectedValue = "HelloWorld!";
        if (!output.contains(expectedValue)) {
            return CheckResult.wrong(WRONG_GET_VALUE +
                    "\nExpected:\n" + expectedValue + "\nYour output:\n" + output);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "1");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "1");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE_EMPTY);
        }

        client = getClient();
        output = client.start("-t", "get", "-i", "1");
        if (!output.toUpperCase().contains(ERROR_STATUS)) {
            return CheckResult.wrong(WRONG_GET_EMPTY_CELL_WITH_ERROR + "\nMay be after deleting a cell you didn't " +
                    "assign an empty value to it.");
        }

        client = getClient();
        output = client.start("-t", "set", "-i", "55", "-m", "Some text here");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_SET_VALUE_TO_CELL_WITH_OK);
        }

        client = getClient();
        output = client.start("-t", "get", "-i", "55");
        expectedValue = "Some text here";
        if (!output.contains(expectedValue)) {
            return CheckResult.wrong(WRONG_GET_VALUE +
                    "\nExpected:\n" + expectedValue + "\nYour output:\n" + output);
        }

        client = getClient();
        output = client.start("-t", "get", "-i", "56");
        if (!output.toUpperCase().contains(ERROR_STATUS)) {
            return CheckResult.wrong(WRONG_GET_EMPTY_CELL_WITH_ERROR);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "55");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "56");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE_EMPTY);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "100");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE_EMPTY);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "1001");
        if (!output.toUpperCase().contains(ERROR_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE_INDEX_OUT_OF_BOUNDS);
        }

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

    private static void stopServer() {
        TestedProgram client = getClient();
        client.start("-t", "exit");
    }
}
