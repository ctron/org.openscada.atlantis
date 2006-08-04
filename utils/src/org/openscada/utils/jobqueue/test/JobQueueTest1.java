package org.openscada.utils.jobqueue.test;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.utils.jobqueue.JobQueue;
import org.openscada.utils.lang.Holder;


public class JobQueueTest1
{
    private void step ( JobQueue jq )
    {
        JobQueue.Job job = jq.getNext ();
        if ( job != null )
        {
            job.getRunnable ().run ();
            jq.completeJob ( job.getId () );
        }
    }
    
    @Test
    public void test1 ()
    {
        JobQueue jq = new JobQueue ();
        
        final Holder<Boolean> complete = new Holder<Boolean>( false );
        jq.addJob ( new Runnable () {

            public void run ()
            {
                complete.value = true;
            }} );
        
        Assert.assertFalse ( "Job not complete", complete.value );
        step ( jq );
        Assert.assertTrue ( "Job complete", complete.value );
    }
    
    @Test
    public void test2 ()
    {
        JobQueue jq = new JobQueue ();
        
        final Holder<Boolean> complete1 = new Holder<Boolean>( false );
        jq.addJob ( new Runnable () {

            public void run ()
            {
                complete1.value = true;
            }} );
        
        final Holder<Boolean> complete2 = new Holder<Boolean>( false );
        jq.addJob ( new Runnable () {

            public void run ()
            {
                complete2.value = true;
            }} );
        
        Assert.assertFalse ( "Job 1 not complete", complete1.value );
        Assert.assertFalse ( "Job 2 not complete", complete2.value );
        step ( jq );
        Assert.assertTrue ( "Job 1 complete", complete1.value );
        Assert.assertFalse ( "Job 2 not complete", complete2.value );
        step ( jq );
        Assert.assertTrue ( "Job 2 complete", complete2.value );
    }
    
    @Test
    public void test3 ()
    {
        JobQueue jq = new JobQueue ();
        
        final Holder<Boolean> complete1 = new Holder<Boolean>( false );
        long id1 = jq.addJob ( new Runnable () {

            public void run ()
            {
                complete1.value = true;
            }} );
        
        final Holder<Boolean> complete2 = new Holder<Boolean>( false );
        long id2 = jq.addJob ( new Runnable () {

            public void run ()
            {
                complete2.value = true;
            }} );
        
        Assert.assertFalse ( "Job 1 not complete", complete1.value );
        Assert.assertFalse ( "Job 2 not complete", complete2.value );
        step ( jq );
        Assert.assertTrue ( "Job 1 complete", complete1.value );
        Assert.assertFalse ( "Job 2 not complete", complete2.value );
        
        boolean rc = jq.removeJob ( id2 );
        Assert.assertEquals ( true, rc );
        
        step ( jq );
        Assert.assertFalse ( "Job 2 not complete", complete2.value );
    }
}
