/**
 * This is included inside a switch statement.
 */

case OP_I2B:
  just_set_top_value ((JBYTE) word2jint(get_top_value()));
  goto LABEL_ENGINELOOP;
case OP_I2S:
case OP_I2C:
  just_set_top_value ((JSHORT) word2jint(get_top_value()));
  goto LABEL_ENGINELOOP;   

#if FP_ARITHMETIC

case OP_I2F:
case OP_L2F:
  // Arguments: 0
  // Stack: -1 +1
  set_top_category1 (jfloat2word ((JFLOAT) word2jint(get_top_value())));
  goto LABEL_ENGINELOOP;
case OP_I2D:
case OP_L2D:
  // Arguments: 0
  // Stack: -1 +1
  set_top_category2 (jfloat2word ((JFLOAT) word2jint(get_top_value())));
  goto LABEL_ENGINELOOP;
case OP_F2I:
case OP_D2I:
  // Arguments: 0
  // Stack: -1 +1
  set_top_category1 ((JINT) word2jfloat(get_top_value()));
  goto LABEL_ENGINELOOP;
case OP_F2L:
case OP_D2L:
  // Arguments: 0
  // Stack: -1 +1
  set_top_category2 ((JINT) word2jfloat(get_top_value()));
  goto LABEL_ENGINELOOP;

#endif

/*end*/


