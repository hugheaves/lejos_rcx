/**
 * This is included inside a switch statement.
 */

case OP_IF_ICMPEQ:
case OP_IF_ACMPEQ:
  // Arguments: 2
  // Stack: -2
  do_isub();
  // Fall through!
case OP_IFEQ:
case OP_IFNULL:
  // Arguments: 2
  // Stack: -1
  do_goto (pop_value() == 0);
  goto LABEL_ENGINELOOP;
case OP_IF_ICMPNE:
case OP_IF_ACMPNE:
  do_isub();
  // Fall through!
case OP_IFNE:
case OP_IFNONNULL:
  do_goto (pop_value() != 0);
  goto LABEL_ENGINELOOP;
case OP_IF_ICMPLT:
  do_isub();
  // Fall through!
case OP_IFLT:
  do_goto (pop_jint() < 0);
  goto LABEL_ENGINELOOP;
case OP_IF_ICMPLE:
  do_isub();
  // Fall through!
case OP_IFLE:
  do_goto (pop_jint() <= 0);
  goto LABEL_ENGINELOOP;
case OP_IF_ICMPGE:
  do_isub();
  // Fall through!
case OP_IFGE:
  do_goto (pop_jint() >= 0);
  goto LABEL_ENGINELOOP;
case OP_IF_ICMPGT:
  do_isub();
  // Fall through!
case OP_IFGT:
  do_goto (pop_jint() > 0);
  goto LABEL_ENGINELOOP;


case OP_JSR:
  // Arguments: 2
  // Stack: +1
  push_category1 (ptr2word (pc + 2));
  // Fall through!
case OP_GOTO:
  // Arguments: 2
  // Stack: +0
  do_goto (true);
  // No pc increment!
  goto LABEL_ENGINELOOP;
case OP_RET:
  // Arguments: 1
  // Stack: +0
  pc = word2ptr (get_local_word (pc[0]));
  #if DEBUG_BYTECODE
  printf ("\n  OP_RET: returning to %d\n", (int) pc);
  #endif
  // No pc increment!
  goto LABEL_ENGINELOOP;

#if FP_ARITHMETIC

case OP_DCMPL:
case OP_DCMPG:
case OP_FCMPL:
case OP_FCMPG:
  // TBD: no distinction between opcodes
  tempStackWord = pop_value();
  do_fcmp (word2jfloat(pop_value()), word2jfloat(tempStackWord), 0);
  goto LABEL_ENGINELOOP;
  
#endif FP_ARITHMETIC

// Notes:
// - Not supported: TABLESWITCH, LOOKUPSWITCH, GOTO_W, JSR_W, LCMP

/*end*/







