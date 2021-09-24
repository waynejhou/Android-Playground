package org.waynezhou.libUtil.event;


public abstract class EventGroup <TEventGroup extends EventGroup<TEventGroup>>  {

    protected EventGroup() {

    }

    public <TEventArgs> TEventGroup on(EventGroupHolderSelector<TEventGroup, TEventArgs> selector, EventListener<TEventArgs> listener){
        selector.select((TEventGroup)this).addOnListener(listener);
        return (TEventGroup)this;
    }

    public <TEventArgs> TEventGroup once(EventGroupHolderSelector<TEventGroup, TEventArgs> selector, EventListener<TEventArgs> listener){
        selector.select((TEventGroup)this).addOnceListener(listener);
        return (TEventGroup)this;
    }

    public <TEventArgs> EventHolder<TEventArgs>.ListenerToken on_(EventGroupHolderSelector<TEventGroup, TEventArgs> selector, EventListener<TEventArgs> listener){
        return selector.select((TEventGroup)this).addOnListener(listener);
    }

    public <TEventArgs> EventHolder<TEventArgs>.ListenerToken once_(EventGroupHolderSelector<TEventGroup, TEventArgs> selector, EventListener<TEventArgs> listener){
        return selector.select((TEventGroup)this).addOnceListener(listener);
    }

    private EventGroup<TEventGroup>.Invoker invoker;
    protected EventGroup<TEventGroup>.Invoker getInvoker(){
        if(invoker==null) invoker = new Invoker((TEventGroup)this);
        return invoker;
    }

    public <TEventArgs> TEventGroup remove(EventGroupHolderSelector<TEventGroup, TEventArgs> selector, EventHolder<TEventArgs>.ListenerToken token){
        selector.select((TEventGroup)this).removeListener(token);
        return (TEventGroup)this;
    }
    public <TEventArgs> TEventGroup remove(EventGroupHolderSelector<TEventGroup, TEventArgs> selector, EventListener<TEventArgs> listener){
        selector.select((TEventGroup)this).removeListener(listener);
        return (TEventGroup)this;
    }

    public TEventGroup apply(EventGroupApplier<TEventGroup> applier){
        TEventGroup group = (TEventGroup)this;
        applier.apply(group);
        return group;
    }


    public class Invoker{
        private final TEventGroup  group;
        private Invoker(TEventGroup group){
            this.group = group;
        }
        public <TEventArgs> void invoke(EventGroupHolderSelector<TEventGroup, TEventArgs> selector, TEventArgs eventArgs){
            selector.select(group).invoke(eventArgs);
        }
    }

    @FunctionalInterface
    public interface EventGroupApplier<TEventGroup extends EventGroup<TEventGroup>> {
        void apply(TEventGroup pack);
    }

    @FunctionalInterface
    public interface EventGroupHolderSelector<TEventGroup extends EventGroup<TEventGroup>, TEventArgs>{
        EventHolder<TEventArgs> select(TEventGroup group);
    }
}
