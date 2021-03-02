package client;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class CommandArgs {
    @Parameter(
            names = "-t",
            description = "Request type",
            required = true,
            validateValueWith = ValidCommand.class
    )
    private String requestType;

    @Parameter(
            names = "-i",
            description = "Cell index"
//            validateValueWith = RequiredWhenNotExit.class
    )
    private int cellIndex;

    @Parameter(
            names = "-m",
            description = "Value to save in the database"
//            validateValueWith = RequiredWhenSet.class
    )
    private String inputValue;

    @Parameter(
            names = "--help",
            help = true
    )
    private boolean help;

    public String getRequestType() {
        return requestType;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public String getInputValue() {
        return inputValue;
    }

    public boolean isHelp() {
        return help;
    }

    public static class ValidCommand implements IValueValidator<String> {
        @Override
        public void validate(String name, String value) throws ParameterException {
            if (!(value.equals("get") || value.equals("set") || value.equals("delete") || value.equals("exit"))) {
                throw new ParameterException("Parameter " + name + " does not correspond to a valid command");
            }
        }
    }

//    public static class RequiredWhenSet implements IValueValidator<String> {
//        @Override
//        public void validate(String name, String value) throws ParameterException {
//            if (requestType.equals("set") && value == null) {
//                throw new ParameterException("Parameter" + name + " is required when using the set command");
//            }
//        }
//    }
//
//    public static class RequiredWhenNotExit implements IValueValidator<Integer> {
//        @Override
//        public void validate(String name, Integer value) throws ParameterException {
//            if (!requestType.equals("exit") && value == null) {
//                throw new ParameterException("Parameter" + name + " is required");
//            }
//        }
//    }
}
