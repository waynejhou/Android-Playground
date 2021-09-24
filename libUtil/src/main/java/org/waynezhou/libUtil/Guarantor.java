package org.waynezhou.libUtil;

import android.util.Pair;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.eventGroup.GuarantorEventGroup;

import java.util.ArrayList;
import java.util.List;

public class Guarantor<T, TErrorReason> {

    private final _GuarantorEventGroup<T, TErrorReason> eventGroup = new _GuarantorEventGroup<>();
    private final EventGroup<GuarantorEventGroup<T, TErrorReason>>.Invoker invoker;

    private static class _GuarantorEventGroup<T, TErrorReason> extends GuarantorEventGroup<T, TErrorReason> {
        public EventGroup<GuarantorEventGroup<T, TErrorReason>>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public GuarantorEventGroup<T, TErrorReason> getEventGroup() {
        return eventGroup;
    }


    //private final GuarantorEventGroup<T, TErrorReason> eventPack = new GuarantorEventGroup<>();
    private final List<Pair<GuarantorReason<T, TErrorReason>, GuarantorChecker<T>>> checkers;

    private Guarantor(List<Pair<GuarantorReason<T, TErrorReason>, GuarantorChecker<T>>> checkers) {
        this.checkers = checkers;
        this.invoker = eventGroup.getInvoker();
    }

    public static class Builder<T, TErrorReason> {
        private final List<Pair<GuarantorReason<T, TErrorReason>, GuarantorChecker<T>>> checkers = new ArrayList<>();

        public Builder<T, TErrorReason> check(GuarantorChecker<T> checker, GuarantorReason<T, TErrorReason> reasonOnFalse) {
            checkers.add(new Pair<>(reasonOnFalse, checker));
            return this;
        }

        public Guarantor<T, TErrorReason> build() {
            return new Guarantor<>(checkers);
        }
    }

    private T t;

    public Guarantor<T, TErrorReason> set(T t) {
        this.t = t;
        return this;
    }

    public void launch() {
        for (Pair<GuarantorReason<T, TErrorReason>, GuarantorChecker<T>> checker : checkers) {
            if (!checker.second.check(t)) {
                invoker.invoke(g->g.notGuaranteed, checker.first.getReason(t));
                return;
            }
        }
        invoker.invoke(g->g.guaranteed, t);
        t = null;
    }
}
