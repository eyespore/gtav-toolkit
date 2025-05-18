package club.pineclone.gtavops.macro.action.impl;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.ActionEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class BlockAction extends Action {
    private final static String ACTION_ID = "action-ext";
    private final AtomicBoolean running;
    private final AtomicLong blockStartTime;
    private boolean blocked = false;
    private final long blockDuration;

    public BlockAction() {
        this(0);
    }

    public BlockAction(long blockDuration) {
        this(new AtomicBoolean(true), blockDuration);
    }

    public BlockAction(AtomicBoolean running, long blockDuration) {
        super(ACTION_ID);
        this.running = running;
        this.blockStartTime = new AtomicLong(0);
        this.blockDuration = blockDuration;
    }

    @Override
    public void activate(ActionEvent event) {
        if (!running.get()) return;
        blocked = true;
    }

    @Override
    public void deactivate(ActionEvent event) {
        if (!running.get()) return;
        blocked = false;
        blockStartTime.set(System.currentTimeMillis());
    }

    public boolean isBlocked() {
        if (blocked) return true;
        long elapsed = System.currentTimeMillis() - blockStartTime.get();
        return elapsed < blockDuration;
    }
}
