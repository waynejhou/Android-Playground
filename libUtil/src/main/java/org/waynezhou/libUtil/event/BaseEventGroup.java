package org.waynezhou.libUtil.event;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseEventGroup<TEventGroup extends BaseEventGroup<TEventGroup>> {
    
    protected BaseEventGroup() {
    
    }
    
    @SuppressWarnings("unchecked")
    @NonNull
    public <TEventArgs> TEventGroup on(
      @NonNull EventGroupHolderSelector<TEventGroup, TEventArgs> selector,
      @NonNull EventListener<TEventArgs> listener
    ) {
        selector.select((TEventGroup) this).addOnListener(listener);
        return (TEventGroup) this;
    }
    
    @SuppressWarnings("unchecked")
    @NonNull
    public <TEventArgs> TEventGroup once(
      @NonNull EventGroupHolderSelector<TEventGroup, TEventArgs> selector,
      @NonNull EventListener<TEventArgs> listener
    ) {
        selector.select((TEventGroup) this).addOnceListener(listener);
        return (TEventGroup) this;
    }
    
    
    @SuppressWarnings("unchecked")
    @NonNull
    public <TEventArgs> EventHolder<TEventArgs>.ListenerToken on_(
      @NonNull EventGroupHolderSelector<TEventGroup, TEventArgs> selector,
      @NonNull EventListener<TEventArgs> listener
    ) {
        return selector.select((TEventGroup) this).addOnListener(listener);
    }
    
    @SuppressWarnings("unchecked")
    @NonNull
    public <TEventArgs> EventHolder<TEventArgs>.ListenerToken once_(
      @NonNull EventGroupHolderSelector<TEventGroup, TEventArgs> selector,
      @NonNull EventListener<TEventArgs> listener
    ) {
        return selector.select((TEventGroup) this).addOnceListener(listener);
    }
    
    public class Invoker {
        private final TEventGroup group;
        
        private Invoker(TEventGroup group) {
            this.group = group;
        }
        
        public <TEventArgs> void invoke(
          @NonNull EventGroupHolderSelector<TEventGroup, TEventArgs> selector,
          TEventArgs eventArgs,
          @NonNull Condition breakAction
        ) {
            selector.select(group).invoke(eventArgs, breakAction);
        }
    
        public <TEventArgs> void invoke(
          @NonNull EventGroupHolderSelector<TEventGroup, TEventArgs> selector,
          TEventArgs eventArgs
        ) {
            invoke(selector, eventArgs, ()->false);
        }
    }
    
    @Nullable
    private BaseEventGroup<TEventGroup>.Invoker invoker;
    
    @SuppressWarnings("unchecked")
    @NonNull
    protected Invoker getInvoker() {
        if (invoker == null) invoker = new Invoker((TEventGroup) this);
        return invoker;
    }
    
    @SuppressWarnings("unchecked")
    @NonNull
    public <TEventArgs> TEventGroup remove(
      @NonNull EventGroupHolderSelector<TEventGroup, TEventArgs> selector,
      @NonNull EventHolder<TEventArgs>.ListenerToken token
    ) {
        selector.select((TEventGroup) this).removeListener(token);
        return (TEventGroup) this;
    }
    
    @SuppressWarnings("unchecked")
    @NonNull
    public <TEventArgs> TEventGroup remove(
      @NonNull EventGroupHolderSelector<TEventGroup, TEventArgs> selector,
      @NonNull EventListener<TEventArgs> listener
    ) {
        selector.select((TEventGroup) this).removeListener(listener);
        return (TEventGroup) this;
    }
    
    @SuppressWarnings("unchecked")
    @NonNull
    public TEventGroup apply(@NonNull EventGroupApplier<TEventGroup> applier) {
        TEventGroup group = (TEventGroup) this;
        applier.apply(group);
        return group;
    }
    

    
    @FunctionalInterface
    public interface EventGroupApplier<TEventGroup extends BaseEventGroup<TEventGroup>> {
        void apply(@NonNull BaseEventGroup<TEventGroup> pack);
    }
    
    @FunctionalInterface
    public interface EventGroupHolderSelector<TEventGroup extends BaseEventGroup<TEventGroup>, TEventArgs> {
        @NonNull
        EventHolder<TEventArgs> select(@NonNull TEventGroup group);
    }

}


