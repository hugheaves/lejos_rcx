/**
 * This is included inside a switch statement.
 */

case OP_ISHL:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_value();
  just_set_top_value (word2jint(get_top_value()) << (tempStackWord & 0x1F));
  goto LABEL_ENGINELOOP;
case OP_ISHR:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_value();
  just_set_top_value (word2jint(get_top_value()) >> (tempStackWord & 0x1F));
  goto LABEL_ENGINELOOP;
case OP_IUSHR:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_value();
  just_set_top_value (get_top_value() >> (tempStackWord & 0x1F));
  goto LABEL_ENGINELOOP;
case OP_IAND:
  tempStackWord = pop_value();
  just_set_top_value (get_top_value() & tempStackWord);
  goto LABEL_ENGINELOOP;
case OP_IOR:
  tempStackWord = pop_value();
  just_set_top_value (get_top_value() | tempStackWord);
  goto LABEL_ENGINELOOP;
case OP_IXOR:
  tempStackWord = pop_value();
  just_set_top_value (get_top_value() ^ tempStackWord);
  goto LABEL_ENGINELOOP;

// Notes:
// - Not supported: LSHL, LSHR, LAND, LOR, LXOR

/*end*/







