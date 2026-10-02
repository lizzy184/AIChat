import ast
import operator


# ============================================================
# Calculator configuration
# ============================================================

MAX_EXPRESSION_LENGTH = 100


# ============================================================
# Supported operators
# ============================================================

_ALLOWED_OPERATORS = {
    ast.Add: operator.add,
    ast.Sub: operator.sub,
    ast.Mult: operator.mul,
    ast.Div: operator.truediv,
}


_ALLOWED_UNARY_OPERATORS = {
    ast.UAdd: operator.pos,
    ast.USub: operator.neg,
}


# ============================================================
# Safe expression evaluator
# ============================================================

def _safe_calculate(node):
    """
    Safely evaluate a mathematical expression.

    Supported:
        +
        -
        *
        /
        ()
        positive numbers
        negative numbers

    This function does NOT use eval().
    """

    # --------------------------------------------------------
    # Number
    # --------------------------------------------------------

    if isinstance(node, ast.Constant):

        if isinstance(node.value, (int, float)):
            return node.value

        raise ValueError(
            "Only numeric values are supported"
        )

    # --------------------------------------------------------
    # Binary operation
    # --------------------------------------------------------

    if isinstance(node, ast.BinOp):

        operator_type = type(node.op)

        if operator_type not in _ALLOWED_OPERATORS:
            raise ValueError(
                f"Unsupported operator: "
                f"{operator_type.__name__}"
            )

        left = _safe_calculate(node.left)

        right = _safe_calculate(node.right)

        # Division by zero
        if operator_type is ast.Div and right == 0:
            raise ZeroDivisionError(
                "Division by zero"
            )

        operation = _ALLOWED_OPERATORS[operator_type]

        return operation(left, right)

    # --------------------------------------------------------
    # Unary operation
    # --------------------------------------------------------

    if isinstance(node, ast.UnaryOp):

        operator_type = type(node.op)

        if operator_type not in _ALLOWED_UNARY_OPERATORS:
            raise ValueError(
                f"Unsupported unary operator: "
                f"{operator_type.__name__}"
            )

        operand = _safe_calculate(node.operand)

        operation = _ALLOWED_UNARY_OPERATORS[operator_type]

        return operation(operand)

    # --------------------------------------------------------
    # Everything else is forbidden
    # --------------------------------------------------------

    raise ValueError(
        f"Unsupported expression: "
        f"{type(node).__name__}"
    )


# ============================================================
# Calculator Tool
# ============================================================

def calculator(expression: str):
    """
    Calculate a mathematical expression safely.

    Supported operators:
        +
        -
        *
        /
        ()

    Example:
        calculator("123 + 456")
    """

    # ========================================================
    # 1. Validate input type
    # ========================================================

    if not isinstance(expression, str):

        return {
            "success": False,
            "error": {
                "code": "INVALID_ARGUMENT",
                "message": "Expression must be a string",
            },
        }

    # ========================================================
    # 2. Remove whitespace
    # ========================================================

    expression = expression.strip()

    # ========================================================
    # 3. Empty expression
    # ========================================================

    if not expression:

        return {
            "success": False,
            "error": {
                "code": "INVALID_ARGUMENT",
                "message": "Expression cannot be empty",
            },
        }

    # ========================================================
    # 4. Maximum length
    # ========================================================

    if len(expression) > MAX_EXPRESSION_LENGTH:

        return {
            "success": False,
            "error": {
                "code": "INVALID_ARGUMENT",
                "message": (
                    "Expression is too long. "
                    f"Maximum length is "
                    f"{MAX_EXPRESSION_LENGTH} characters."
                ),
            },
        }

    # ========================================================
    # 5. Parse expression
    # ========================================================

    try:

        tree = ast.parse(
            expression,
            mode="eval",
        )

    except SyntaxError:

        return {
            "success": False,
            "error": {
                "code": "INVALID_EXPRESSION",
                "message": (
                    "Invalid mathematical expression"
                ),
            },
        }

    # ========================================================
    # 6. Calculate safely
    # ========================================================

    try:

        result = _safe_calculate(tree.body)

    except ZeroDivisionError:

        return {
            "success": False,
            "error": {
                "code": "DIVISION_BY_ZERO",
                "message": "Division by zero",
            },
        }

    except ValueError as e:

        return {
            "success": False,
            "error": {
                "code": "UNSUPPORTED_EXPRESSION",
                "message": str(e),
            },
        }

    except Exception as e:

        return {
            "success": False,
            "error": {
                "code": "CALCULATION_FAILED",
                "message": str(e),
            },
        }

    # ========================================================
    # 7. Return result
    # ========================================================

    return {
        "success": True,
        "data": {
            "expression": expression,
            "result": result,
        },
    }


# ============================================================
# Tool Schema
# ============================================================

CALCULATOR_TOOL = {
    "type": "function",
    "function": {
        "name": "calculator",

        "description": (
            "Calculate mathematical expressions and "
            "return the exact result. "
            "Use this tool when the user asks "
            "for arithmetic or numerical calculations."
        ),

        "parameters": {
            "type": "object",

            "properties": {
                "expression": {
                    "type": "string",
                    "description": (
                        "A mathematical expression using "
                        "+, -, *, /, and parentheses."
                    ),
                }
            },

            "required": [
                "expression"
            ],
        },
    },
}