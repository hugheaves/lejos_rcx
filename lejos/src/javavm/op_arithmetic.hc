/**
 * This is included inside a switch statement.
 */

case OP_ISUB:
  // Arguments: 0
  // Stack: -2 +1
  just_set_top_value (-word2jint(get_top_value()));
  // Fall through!
case OP_IADD:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_value();
  just_set_top_value (word2jint(get_top_value()) + word2jint(tempStackWord));
  goto LABEL_ENGINELOOP;
case OP_IMUL:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_value();
  just_set_top_value (word2jint(get_top_value()) * word2jint(tempStackWord));
  goto LABEL_ENGINELOOP;
case OP_IDIV:
case OP_IREM:
  tempInt = word2jint(pop_value());
  if (tempInt == 0)
  {
    throw_exception (arithmeticException);
    goto LABEL_ENGINELOOP;
  }
  just_set_top_value ((*(pc-1) == OP_IDIV) ? word2jint(get_top_value()) / tempInt :
                                            word2jint(get_top_value()) % tempInt);
  goto LABEL_ENGINELOOP;
case OP_INEG:
  just_set_top_value (-word2jint(get_top_value()));
  goto LABEL_ENGINELOOP;

#if FP_ARITHMETIC

case OP_FSUB:
case OP_DSUB:
  just_set_top_value (jfloat2word(-word2jfloat(get_top_value())));
  // Fall through!
case OP_FADD:
case OP_DADD:
  tempStackWord = pop_value();
  just_set_top_value (jfloat2word(word2jfloat(get_top_value()) + 
                     word2jfloat(tempStackWord)));
  goto LABEL_ENGINELOOP;
case OP_FMUL:
case OP_DMUL:
  tempStackWord = pop_value();
  just_set_top_value (jfloat2word(word2jfloat(get_top_value()) * 
                     word2jfloat(tempStackWord)));
  goto LABEL_ENGINELOOP;
case OP_FDIV:
case OP_DDIV:
  // TBD: no division by zero?
  tempStackWord = pop_value();
  just_set_top_value (jfloat2word(word2jfloat(get_top_value()) / 
                     word2jfloat(tempStackWord)));
  goto LABEL_ENGINELOOP;
case OP_FNEG:
case OP_DNEG:
  just_set_top_value (jfloat2word(-word2jfloat(get_top_value())));
  goto LABEL_ENGINELOOP;

#endif FP_ARITHMETIC

// Notes:
// - Not supported: LADD, LSUB, LMUL, LREM, FREM, DREM
// - Operations on doubles are truncated to low float

/*end*/







