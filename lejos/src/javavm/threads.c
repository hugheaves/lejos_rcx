
#include "types.h"
#include "trace.h"
#include "constants.h"
#include "specialsignatures.h"
#include "specialclasses.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "configure.h"
#include "interpreter.h"
#include "memory.h"

#define NO_OWNER 0

#define get_stack_frame() ((StackFrame *) (currentThread->currentStackFrame))

/**
 * Thread currently being executed by engine().
 */
Thread* currentThread;

StackFrame *current_stackframe()
{
  byte arraySize;

  arraySize = currentThread->stackFrameArraySize;
  if (arraySize == 0)
    return null;
  return stackframe_array() + (arraySize - 1);
}

inline byte get_thread_id (Object *obj)
{
  return (byte) ((obj->syncInfo >> THREAD_SHIFT) & THREAD_MASK);
}

inline void set_thread_id (Object *obj, byte threadId)
{
  obj->syncInfo = (obj->syncInfo & ~THREAD_MASK) | 
                  ((TWOBYTES) threadId << THREAD_SHIFT);
}

inline void inc_monitor_count (Object *obj)
{
  obj->syncInfo = (obj->syncInfo & ~COUNT_MASK) | 
                   ((obj->syncInfo & COUNT_MASK) + 1);
}

inline void set_monitor_count (Object *obj, byte count)
{
  obj->syncInfo = (obj->syncInfo & ~COUNT_MASK) | count;
}

void init_thread (Thread *thread)
{
  thread->stackFrameArray = ptr2word (new_primitive_array (T_STACKFRAME, MAX_STACK_FRAMES));
  thread->stackArray = ptr2word (new_primitive_array (T_INT, STACK_SIZE));

  #ifdef VERIFY
  assert (is_array (word2obj (thread->stackFrameArray)), THREADS0);
  assert (is_array (word2obj (thread->stackFrameArray)), THREADS1);
  #endif

  thread->stackFrameArraySize = 0;
  thread->state = STARTED;
  if (currentThread == null)
  {
    currentThread = thread;
    #if DEBUG_THREADS
    printf ("First-time init of currentThread: %d\n", (int) currentThread);
    #endif
  }
  else
  {
    thread->nextThread = currentThread->nextThread;
  }
  currentThread->nextThread = ptr2word (thread);
}

/**
 * Switches to next thread.
 * @return false iff there are no live threads
 *         to switch to.
 */
void switch_thread()
{
  Thread *anchorThread;
  Thread *nextThread;
  StackFrame *stackFrame;
  boolean liveThreadExists;

  #if DEBUG_THREADS
  printf ("Switch thread: currentThread: %d (%d)\n", (int) currentThread,
          (int) (currentThread != null));
  #endif

  #ifdef VERIFY
  assert (currentThread != null, THREADS0);
  #endif
  
  anchorThread = currentThread;
  liveThreadExists = false;
  // Save context information
  stackFrame = current_stackframe();

  #if DEBUG_THREADS
  printf ("switchThread: current stack frame: %d\n", (int) stackFrame);
  #endif
  
  #ifdef VERIFY
  assert (stackFrame != null || currentThread->state == STARTED,
          THREADS4);
  #endif

  if (stackFrame != null)
  {
    stackFrame->pc = pc;
    stackFrame->stackTop = stackTop;

    #if DEBUG_THREADS
    printf ("Saving stackFrame before switching:\n"
            "-- pc: %d\n"
            "-- stackTop: %d\n",
            (int) pc, (int) stackTop);
    #endif
  }

  // Loop until a RUNNING frame is found
 LABEL_TASKLOOP:
  nextThread = (Thread *) word2ptr (currentThread->nextThread);
  if (nextThread->state == WAITING)
  {
    #ifdef VERIFY
    assert (nextThread->waitingOn != JNULL, THREADS3);
    #endif

    if (get_thread_id((Object *) word2ptr (nextThread->waitingOn)) == NO_OWNER)
    {
      nextThread->state = RUNNING;
      #ifdef SAFE
      nextThread->waitingOn = JNULL;
      #endif
    }
  }

  if (nextThread->state == DEAD)
  {
    if (nextThread == anchorThread && !liveThreadExists)
    {
      #if DEBUG_THREADS
      printf ("switch_thread: all threads are dead: %d\n", (int) nextThread);
      #endif
      gMustExit = true;
      return;
    }
    #if REMOVE_DEAD_THREADS
    free_array ((Object *) word2ptr (nextThread->stackFrameArray));
    free_array ((Object *) word2ptr (nextThread->stackArray));

    #ifdef SAFE
    nextThread->stackFrameArray = JNULL;
    nextThread->stackArray = JNULL;
    #endif SAFE

    nextThread = (Thread *) word2ptr (nextThread->nextThread);
    currentThread->nextThread = ptr2word (nextThread);
    #endif REMOVE_DEAD_THREADS
  }
  else
  {
    liveThreadExists = true;
  }  

  currentThread = nextThread;
  if (currentThread->state == STARTED)
  {
    currentThread->state = RUNNING;
    if (currentThread == bootThread)
    {
      ClassRecord *classRecord;

      classRecord = get_class_record (ENTRY_CLASS);
      // Push stack frame for main method:
      dispatch_special (classRecord, find_method (classRecord, MAIN_V), null);
      // Push another if necessary for the static initializer:
      dispatch_static_initializer (classRecord, pc);
    }
    else
    {
      dispatch_virtual ((Object *) currentThread, RUN_V, null);
    }
  }

  #if DEBUG_THREADS
  printf ("switch_thread: considered thread %d: %d\n", (int) currentThread,
          (int) (currentThread->state == RUNNING));
  #endif

  if (currentThread->state != RUNNING)
    goto LABEL_TASKLOOP;
  stackFrame = current_stackframe();
  pc = stackFrame->pc;
  stackTop = stackFrame->stackTop;
  localsBase = stackFrame->localsBase;
}

/**
 * currentThread enters obj's monitor.
 * Note that this operation is atomic as far as the program is concerned.
 */
void enter_monitor (Object* obj)
{
  byte owner;
  byte tid;
  owner = get_thread_id (obj);
  tid = currentThread->threadId;
  if (owner != NO_OWNER && tid != owner)
  {
    currentThread->state = WAITING;
    currentThread->waitingOn = ptr2word (obj);
    // Gotta yield
    switch_thread();
    return;
  }
  set_thread_id (obj, tid);
  inc_monitor_count (obj);
}

void exit_monitor (Object* obj)
{
  byte newMonitorCount;

  #ifdef VERIFY
  assert (get_thread_id(obj) == currentThread->threadId, THREADS1);
  assert (get_monitor_count(obj) > 0, THREADS2);
  #endif

  newMonitorCount = get_monitor_count(obj)-1;
  if (newMonitorCount == 0)
    set_thread_id (obj, NO_OWNER);
  set_monitor_count (obj, newMonitorCount);
}





