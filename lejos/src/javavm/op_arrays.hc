/**
 * This is included inside a switch statement.
 */

case OP_NEWARRAY:
  // Stack size: unchanged
  // Arguments: 1
  set_top_ref (obj2ref(new_primitive_array (*pc++, get_top_value())));
  // Exceptions are taken care of
  goto LABEL_ENGINELOOP;
case OP_MULTIANEWARRAY:
  // Stack size: -N + 1
  // Arguments: 3
  tempByte = pc[2] - 1;
  tempBytePtr = (byte *) new_multi_array (pc[0], pc[1], pc[2], get_stack_ptr() - tempByte);
  pop_values (tempByte);
  set_top_ref (ptr2ref (tempBytePtr));
  pc += 3;
  goto LABEL_ENGINELOOP;
case OP_AALOAD:
  // Stack size: -2 + 1
  // Arguments: 0
  if (!array_load_helper())
    goto LABEL_ENGINELOOP;
  // tempBytePtr and tempInt set by call above
  set_top_ref (word_array(tempBytePtr)[tempInt]);
  goto LABEL_ENGINELOOP;
case OP_IALOAD:
case OP_FALOAD:
  // Stack size: -2 + 1
  // Arguments: 0
  if (!array_load_helper())
    goto LABEL_ENGINELOOP;
  set_top_category1 (word_array(tempBytePtr)[tempInt]);
  goto LABEL_ENGINELOOP;
case OP_LALOAD:
case OP_DALOAD:
  // Stack size: -2 + 1
  // Arguments: 0
  if (!array_load_helper())
    goto LABEL_ENGINELOOP;
  set_top_category2 (word_array(tempBytePtr)[tempInt]);
  goto LABEL_ENGINELOOP;
case OP_CALOAD:
case OP_SALOAD:
  if (!array_load_helper())
    goto LABEL_ENGINELOOP;
  set_top_category1 (jshort_array(tempBytePtr)[tempInt]);
  goto LABEL_ENGINELOOP;
case OP_BALOAD:
  if (!array_load_helper())
    goto LABEL_ENGINELOOP;
  set_top_category1 (jbyte_array(tempBytePtr)[tempInt]);
  goto LABEL_ENGINELOOP;
case OP_AASTORE:
case OP_IASTORE:
case OP_FASTORE:
case OP_DASTORE:
case OP_LASTORE:
  // Stack size: -3
  tempStackWord = pop_value();
  if (!array_store_helper())
    goto LABEL_ENGINELOOP;
  jint_array(tempBytePtr)[tempInt] = tempStackWord;
  goto LABEL_ENGINELOOP;
case OP_CASTORE:
case OP_SASTORE:
  // Stack size: -3
  tempStackWord = pop_value();
  if (!array_store_helper())
    goto LABEL_ENGINELOOP;
  jshort_array(tempBytePtr)[tempInt] = tempStackWord;
  goto LABEL_ENGINELOOP;
case OP_BASTORE:
  // Stack size: -3
  tempStackWord = pop_value();
  if (!array_store_helper())
    goto LABEL_ENGINELOOP;
  jbyte_array(tempBytePtr)[tempInt] = tempStackWord;
  goto LABEL_ENGINELOOP;
case OP_ARRAYLENGTH:
  // Stack size: -1 + 1
  // Arguments: 0
  {
    REFERENCE tempRef;

    tempRef = get_top_ref();
    
    //printf ("ARRAYLENGTH for %d\n", (int) tempRef); 
    
    if (tempRef == JNULL)
      throw_exception (nullPointerException);
    else     
      set_top_category1 (get_array_length (word2obj (tempRef)));
  }
  goto LABEL_ENGINELOOP;


// Notes:
// * OP_ANEWARRAY is changed to OP_NEWARRAY of data type 0, plus a NOP.

/*end*/







