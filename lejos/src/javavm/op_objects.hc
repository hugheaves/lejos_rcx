/**
 * This is included inside a switch statement.
 */

case OP_NEW:
  // Stack: +1
  // Arguments: 2
  // Hi byte unused
  tempBytePtr = (byte *) new_object_checked (pc[1], pc - 1);
  if (tempBytePtr != JNULL)
  { 
    #if 0
    trace (-1, (short) pc[1], 1);
    trace (-1, (short) tempBytePtr, 2);
    trace (-1, get_class_index((Object *) tempBytePtr), 3);
    #endif
    push_ref (ptr2ref(tempBytePtr));
    pc += 2;
  }
  goto LABEL_ENGINELOOP;
case OP_GETSTATIC:
case OP_PUTSTATIC:
  // Stack: +1 or +2 for GETSTATIC, -1 or -2 for PUTSTATIC
  {
    STATICFIELD fieldRecord;
    byte *fbase1 = null;
    byte fieldType;
    byte fieldSize;
    byte category;

    #if DEBUG_FIELDS
    printf ("---  GET/PUTSTATIC --- (%d, %d)\n", (int) pc[0], (int) pc[1]);
    #endif

    if (dispatch_static_initializer (get_class_record (pc[0]), pc - 1))
      goto LABEL_ENGINELOOP;
    fieldRecord = ((STATICFIELD *) get_static_fields_base())[pc[1]];

    fieldType = (fieldRecord >> 12) & 0x0F;
    category = typeCategory[fieldType];
    fieldSize = typeSize[fieldType];
    #ifdef VERIFY
    assert (fieldSize <= 4, INTERPRETER7);
    #endif

    fbase1 = get_static_state_base() + get_static_field_offset (fieldRecord);

    #if DEBUG_FIELDS
    printf ("fieldSize  = %d\n", (int) fieldSize);
    printf ("fbase1  = %d\n", (int) fbase1);
    #endif

    if (*(pc-1) == OP_GETSTATIC)
    {
      make_word (fbase1, fieldSize, &tempStackWord);
      push_value (tempStackWord, category);
    }
    else
    {
      store_word (fbase1, fieldSize, pop_value());
    }
    pc += 2;
  }
  goto LABEL_ENGINELOOP;
case OP_GETFIELD:
  {
    byte *fbase2 = null;
    byte fieldType;
    byte fieldSize;

    tempStackWord = get_top_ref();
    if (tempStackWord == JNULL)
    {
      throw_exception (nullPointerException);
      goto LABEL_ENGINELOOP;
    }
    fieldType = get_pgfield_type(pc[0]);
    fieldSize = typeSize[fieldType];
    #ifdef VERIFY
    assert (fieldSize <= 4, INTERPRETER8);
    #endif

    fbase2 = ((byte *) word2ptr (tempStackWord)) + 
                get_pgfield_offset(pc[0], pc[1]);

    #ifdef DEBUG_FIELDS
    printf ("--- PUTFIELD ---\n");
    printf ("fieldType: %d\n", (int) fieldType);
    printf ("fieldSize: %d\n", (int) fieldSize);
    printf ("reference: %d\n", (int) tempStackWord);
    #endif

    make_word (fbase2, fieldSize, &tempStackWord);
   
    #if 0
    printf ("### get_field base=%d size=%d pushed=%d\n", (int) fbase2, (int) fieldSize, (int) tempStackWord);
    #endif

    set_top_value (tempStackWord, typeCategory[fieldType]);
    pc += 2;
  }
  goto LABEL_ENGINELOOP;
case OP_PUTFIELD:
  {
    byte *fbase3 = null;
    STACKWORD objRef;
    byte fieldType;
    byte fieldSize;
    boolean isLongWord;

    fieldType = get_pgfield_type(pc[0]);
    fieldSize = typeSize[fieldType];

    #ifdef VERIFY
    assert (fieldSize <= 4, INTERPRETER9);
    #endif

    // TBD: For some unknown reason, without the
    // following, the RCX hangs.

    isLongWord = (fieldSize > 4);

    objRef = get_ref_at (1);

    #ifdef DEBUG_FIELDS
    printf ("--- PUTFIELD ---\n");
    printf ("fieldType: %d\n", (int) fieldType);
    printf ("fieldSize: %d\n", (int) fieldSize);
    printf ("reference: %d\n", (int) objRef);
    #endif

    if (objRef == JNULL)
    {
      throw_exception (nullPointerException);
      goto LABEL_ENGINELOOP;
    }
    fbase3 = ((byte *) word2ptr (objRef)) +
                get_pgfield_offset (pc[0], pc[1]); 

    if (isLongWord) {
       // This is always false, though.
       store_word (fbase3 + 4, 4, pop_value());
    }

    store_word (fbase3, fieldSize, pop_value());
    pop_value();
    pc += 2;
  }
  goto LABEL_ENGINELOOP;
case OP_INSTANCEOF:
  // Stack: unchanged
  // Arguments: 2
  // Ignore hi byte
  set_top_category1 (instance_of (word2obj (get_top_ref()),  pc[1]));
  pc += 2;
  goto LABEL_ENGINELOOP;
case OP_CHECKCAST:
  // Stack: -1 +1 (same)
  // Arguments: 2
  // Ignore hi byte
  pc++;
  tempStackWord = get_top_ref();
  if (tempStackWord != JNULL && !instance_of (word2obj (tempStackWord), pc[0]))
    throw_exception (classCastException);
  pc++;
  goto LABEL_ENGINELOOP;

// Notes:
// - NEW, INSTANCEOF, CHECKCAST: 8 bits ignored, 8-bit class index
// - GETSTATIC and PUTSTATIC: 8-bit class index, 8-bit static field record
// - GETFIELD and PUTFIELD: 4-bit field type, 12-bit field data offset

/*end*/








