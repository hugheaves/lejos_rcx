
#include "types.h"
#include "trace.h"
#include "constants.h"
#include "specialsignatures.h"
#include "specialclasses.h"
#include "stack.h"
#include "memory.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "configure.h"
#include "interpreter.h"
#include "exceptions.h"
#include "systime.h"

sensor_t sensors[3];

extern void reset_rcx_serial();

/**
 * NOTE: The technique is not the same as that used in TinyVM.
 */
void dispatch_native (TWOBYTES signature, STACKWORD *paramBase)
{
  switch (signature)
  {
    case start_4_5V:
      init_thread ((Thread *) word2ptr(paramBase[0]));
      return;
    case yield_4_5V:
      switch_thread();
      return;
    case sleep_4J_5V:
      sleep_thread (paramBase[1]);
      switch_thread();
      return;
    case getPriority_4_5I:
      push_category1 (get_thread_priority ((Thread*)word2obj(paramBase[0])));
      return;
    case setPriority_4I_5V:
      {
        STACKWORD p = (STACKWORD)paramBase[1];
        if (p > MAX_PRIORITY || p < MIN_PRIORITY)
          throw_exception(illegalArgumentException);
        else
          set_thread_priority ((Thread*)word2obj(paramBase[0]), p);
      }
      return;
    case currentThread_4_5Ljava_3lang_3Thread_2:
      push_ref(ptr2ref(currentThread));
      return;
    case interrupt_4_5V:
      interrupt_thread((Thread*)word2obj(paramBase[0]));
      return;
    case interrupted_4_5Z:
      push_category1(currentThread->interrupted);
      return;
    case isInterrupted_4_5Z:
      push_category1(((Thread*)word2ptr(paramBase[0]))->interrupted);
      return;
    case setDaemon_4Z_5V:
      ((Thread*)word2ptr(paramBase[0]))->daemon = (JBYTE)paramBase[1];
      return;
    case isDaemon_4_5Z:
      push_category1(((Thread*)word2ptr(paramBase[0]))->daemon);
      return;
    case exit_4I_5V:
      schedule_request(REQUEST_EXIT);
      return;
    case join_4_5V:
      join_thread((Thread*)word2obj(paramBase[0]));
      return;
    case join_4J_5V:
      join_thread((Thread*)word2obj(paramBase[0]));
      return;
    case currentTimeMillis_4_5J:
      push_category2 (sys_time);
      return;

#if 0

      case ARRAYCOPY_V:
      {
	Object *arr1;
	Object *arr2;
	byte    elemSize = 0;
	
	arr1 = word2obj (paramBase[0]);
	arr2 = word2obj (paramBase[2]);
	if (arr1 == JNULL || arr2 == JNULL)
	{
	  throw_exception (nullPointerException);
	  return;
	}
        if (!is_array (arr1) || !is_array (arr2) ||
	    (elemSize = get_element_size (arr1)) != get_element_size (arr2))
	{
	  throw_exception (classCastException);
	  return;
	}
	
	  
	###
      }
      break;

#endif

    case callRom0_4S_5V:
      __rcall0 (paramBase[0]);
      return;      
    case callRom1_4SS_5V:
      __rcall1 (paramBase[0], paramBase[1]);
      return;      
    case callRom2_4SSS_5V:
      #if 0
      trace (-1, (TWOBYTES) paramBase[0], 6);
      trace (-1, (TWOBYTES) paramBase[1], 7);
      trace (-1, (TWOBYTES) paramBase[2] - 0xF010, 8);
      #endif
      __rcall2 (paramBase[0], paramBase[1], paramBase[2]);
      return;      
    case callRom3_4SSSS_5V:
      __rcall3 (paramBase[0], paramBase[1], paramBase[2], paramBase[3]);
      return;
    case callRom4_4SSSSS_5V:
      __rcall4 (paramBase[0], paramBase[1], paramBase[2], paramBase[3], paramBase[4]);
      return;
    case readMemoryByte_4I_5B:
      push_category1 ((STACKWORD) *((byte *) word2ptr(paramBase[0])));
      return;
    case writeMemoryByte_4IB_5V:
      *((byte *) word2ptr(paramBase[0])) = (byte) (paramBase[1] & 0xFF);
      return;
    case setMemoryBit_4III_5V:
      *((byte *)word2ptr(paramBase[0])) =
        ( *((byte *)word2ptr(paramBase[0])) & (~(1<<paramBase[1])) ) | (((paramBase[2] != 0) ? 1 : 0) <<paramBase[1]);
      return;      
    case getDataAddress_4Ljava_3lang_3Object_2_5I:
      push_category1 (ptr2word (((byte *) word2ptr (paramBase[0])) + HEADER_SIZE));
      return;
    case resetSerial_4_5V:
      reset_rcx_serial();
      return;
    case readSensorValue_4II_5I:
      // Parameters: int romId (0..2), int requestedValue (0..2).
      {
	short pId;
	
	pId = paramBase[0];
	if (pId >= 0 && pId < 3)
	{
          sensor_t *sensor;
	  
	  sensor = &(sensors[pId]);
	  switch ((byte) paramBase[1])
	  {
	    case 0:
	      push_category1 ((JINT) sensor->raw);
	      return;
	    case 1:
	      push_category1 ((JINT) sensor->value);
	      return;
	    case 2:
	      push_category1 (sensor->boolean);
	      return;
	  }
	}
      }
      push_category1 (0);
      return;
    case setSensorValue_4III_5V:
      // Arguments: int romId (1..3), int value, int requestedValue (0..3) 
      {
	short pId;
	
	pId = paramBase[0];
	if (pId >= 0 && pId < 3)
	{
          sensor_t *sensor;
	  STACKWORD value;
	  
	  value = paramBase[1];
	  sensor = &(sensors[pId]);
	  
	  switch ((byte) paramBase[2])
	  {
            case 0:
	      sensor -> mode = value;
	      return;
            case 1:	      
	      sensor -> type = value;
	      return;
	    case 2:
              sensor -> value = (short) (JINT) value;
	      return;
	    case 3:
              sensor -> boolean = value;
	      return;
	  }
	}
      }
      return;
    case freeMemory_4_5J:
      push_category2 (getHeapFree());
      return;
    case totalMemory_4_5J:
      push_category2 (getHeapSize());
      return;
    case getRuntime_4_5Ljava_3lang_3Runtime_2:
      push_ref(ptr2ref(runtime));
      return;
    case assert_4Ljava_3lang_3String_2Z_5V:
      if (!paramBase[1])
      {
        throw_exception(error);
      }
      return;
    case assertEQ_4Ljava_3lang_3String_2II_5V:
      if (paramBase[1] != paramBase[2])
      {
        throw_exception(error);
      }
      return;
    default:
      throw_exception (noSuchMethodError);
      return;
  }  
} 
