/* Common stack operations */

#include "configure.h"
#include "threads.h"
#include "interpreter.h"
#include "memory.h"
#include "language.h"

#ifndef _STACK_H
#define _STACK_H

/* Stack entry types */

#define SET_REFERENCE 3 /*reference*/
#define SET_CATEGORY1 1 /*int,float*/
#define SET_CATEGORY2 2 /*long,double*/

#define get_local_word(IDX_)       (localsBase[(IDX_)])
#define get_local_ref(IDX_)        (localsBase[(IDX_)])
#define inc_local_word(IDX_,NUM_)  (localsBase[(IDX_)] += (NUM_))
#define just_set_top_value(WRD_)    (stackTop[0] = (WRD_))
#define get_top_value()             (stackTop[0])
#define get_top_ref()              (stackTop[0])
#define get_value_at(DOWN_)         (*(stackTop-(DOWN_)))
#define get_ref_at(DOWN_)          *(stackTop-(DOWN_))
#define get_stack_ptr()            (stackTop)
#define get_stack_ptr_at(DOWN_)    (stackTop-(DOWN_))
#define get_category_ptr()           (stackEntryType)
#define get_category_ptr_at(DOWN_)   (stackEntryType-(DOWN_))

// Note: The following locals should only be accessed
// in this header file.

extern STACKWORD *localsBase;
extern STACKWORD *stackTop;
extern byte      *stackEntryType;
extern byte      *stackEntryTypeBase;

/**
 * Clears the operand stack for the given stack frame.
 */
static inline void init_sp (StackFrame *stackFrame, MethodRecord *methodRecord)
{
  stackTop = stackFrame->localsBase + methodRecord->numLocals - 1;
  stackEntryType = stackFrame->stackEntryTypeBase + methodRecord->numLocals - 1;
}

/**
 * Clears/initializes the operand stack at the bottom-most stack frame,
 * and pushes a void (unitialized) element, which should be overriden
 * immediately with set_top_value or set_top_ref.
 */
static inline void init_sp_pv (void)
{
  stackTop = stack_array();
  stackEntryType = stack_entry_type_array();
}

/**
 * With stack cleared, checks for stack overflow in given method.
 */
static inline boolean is_stack_overflow (MethodRecord *methodRecord)
{
  return (stackTop + methodRecord->maxOperands) >= (stack_array() + STACK_SIZE);
}

static inline void update_stack_frame (StackFrame *stackFrame)
{
  stackFrame->stackTop = stackTop;
  stackFrame->stackEntryType = stackEntryType;
  stackFrame->pc = pc;
}  

static inline void update_registers (StackFrame *stackFrame)
{
  pc = stackFrame->pc;
  stackTop = stackFrame->stackTop;
  localsBase = stackFrame->localsBase;
  stackEntryType = stackFrame->stackEntryType;
  stackEntryTypeBase = stackFrame->stackEntryTypeBase;
}

/**--**/

static inline void update_constant_registers (StackFrame *stackFrame)
{
  localsBase = stackFrame->localsBase;
  stackEntryTypeBase = stackFrame->stackEntryTypeBase;
}

static inline void push_ref (const REFERENCE word)
{
  *(++stackTop) = word;
  *(++stackEntryType) = SET_REFERENCE;
}

static inline void push_value (const REFERENCE word, byte category)
{
  *(++stackTop) = word;
  *(++stackEntryType) = category;
}

static inline void push_category1 (const STACKWORD word)
{
  *(++stackTop) = word;
  *(++stackEntryType) = SET_CATEGORY1;
}

static inline void push_category2 (const STACKWORD word)
{
  *(++stackTop) = word;
  *(++stackEntryType) = SET_CATEGORY2;
}

static inline STACKWORD pop_value (void)
{
  --stackEntryType;
  return *stackTop--;
}

static inline JINT pop_jint (void)
{
  --stackEntryType;
  return word2jint(*stackTop--);
}

static inline void pop_values (byte aNum)
{
  stackEntryType -= aNum;
  stackTop -= aNum;
}

static inline void push_void (void)
{
  *(++stackEntryType) = SET_CATEGORY1;
  ++stackTop;
}

static inline void set_top_ref (REFERENCE aRef)
{
  *stackEntryType = SET_REFERENCE;
  *stackTop = aRef;
}

static inline void set_top_category1 (STACKWORD aWord)
{
  *stackEntryType = SET_CATEGORY1;
  *stackTop = aWord;
}

static inline void set_top_category2 (STACKWORD aWord)
{
  *stackEntryType = SET_CATEGORY2;
  *stackTop = aWord;
}

static inline void set_top_value (STACKWORD aWord, byte entryType)
{
  *stackEntryType = entryType;
  *stackTop = aWord;
}

static inline void dup (void)
{
  stackTop++;
  *stackTop = *(stackTop-1);
  stackEntryType++;
  *stackEntryType = *(stackEntryType-1);
}

static inline void dup_x1 (void)
{
  stackTop++;
  *stackTop = *(stackTop-1);
  *(stackTop-1) = *(stackTop-2);
  *(stackTop-2) = *stackTop;
  stackEntryType++;
  *stackEntryType = *(stackEntryType-1);
  *(stackEntryType-1) = *(stackEntryType-2);
  *(stackEntryType-2) = *stackEntryType;
}

static inline void swap (void)
{
  tempStackWord = *stackTop;
  *stackTop = *(stackTop-1);
  *(stackTop-1) = tempStackWord;
  tempStackWord = *stackEntryType;
  *stackEntryType = *(stackEntryType-1);
  *(stackEntryType-1) = tempStackWord;
}

static inline void dup2 (void)
{
   if (*stackEntryType == SET_CATEGORY2)
       dup();
   else
   {
       stackTop[1] = *(stackTop-1);
       stackTop[2] = stackTop[0];
       stackTop += 2;
       stackEntryType[1] = *(stackEntryType-1);
       stackEntryType[2] = stackEntryType[0];
       stackEntryType += 2;
   }
}

static inline void pop2 (void)
{
   if (*stackEntryType == SET_CATEGORY2)
      pop_value();
   else
   {
      pop_value();
      pop_value();
   }
}

static inline void set_local_value (byte aIndex, STACKWORD aWord)
{
  localsBase[aIndex] = aWord;
  stackEntryTypeBase[aIndex] = SET_CATEGORY1;
}

static inline void set_local_ref (byte aIndex, REFERENCE aWord)
{
  localsBase[aIndex] = aWord;
  stackEntryTypeBase[aIndex] = SET_REFERENCE;
}


#endif




