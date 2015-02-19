package passambler.parser;

public class ParserRules {
    public static ParserRules RULES_NONE = new ParserRules();

    private boolean evaluationAllowed = true;

    private boolean forStatementAllowed = true;

    private boolean whileStatementAllowed = true;

    private boolean ifStatementAllowed = true;

    private boolean variableAssignmentAllowed = true;

    private boolean classDeclarationAllowed = true;

    private boolean procedureDeclarationAllowed = true;

    private boolean returnStatementAllowed = true;

    public ParserRules setEvaluationAllowed(boolean allowed) {
        evaluationAllowed = allowed;

        return this;
    }

    public ParserRules setForStatementAllowed(boolean allowed) {
        forStatementAllowed = allowed;

        return this;
    }

    public ParserRules setWhileStatementAllowed(boolean allowed) {
        whileStatementAllowed = allowed;

        return this;
    }

    public ParserRules setIfStatementAllowed(boolean allowed) {
        ifStatementAllowed = allowed;

        return this;
    }

    public ParserRules setVariableAssignmentAllowed(boolean allowed) {
        variableAssignmentAllowed = allowed;

        return this;
    }

    public ParserRules setClassDeclarationAllowed(boolean allowed) {
        classDeclarationAllowed = allowed;

        return this;
    }

    public ParserRules setProcedureDeclarationAllowed(boolean allowed) {
        procedureDeclarationAllowed = allowed;

        return this;
    }

    public ParserRules setReturnStatementAllowed(boolean allowed) {
        returnStatementAllowed = allowed;

        return this;
    }

    public boolean isEvaluationAllowed() {
        return evaluationAllowed;
    }

    public boolean isForStatementAllowed() {
        return forStatementAllowed;
    }

    public boolean isWhileStatementAllowed() {
        return whileStatementAllowed;
    }

    public boolean isIfStatementAllowed() {
        return ifStatementAllowed;
    }

    public boolean isVariableAssignmentAllowed() {
        return variableAssignmentAllowed;
    }

    public boolean isClassDeclarationAllowed() {
        return classDeclarationAllowed;
    }

    public boolean isProcedureDeclarationAllowed() {
        return procedureDeclarationAllowed;
    }

    public boolean isReturnStatementAllowed() {
        return returnStatementAllowed;
    }
}
