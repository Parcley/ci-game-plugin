package hudson.plugins.cigame;

import hudson.model.AbstractBuild;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.Iterator;
import hudson.model.Run;
import hudson.model.User;
import hudson.plugins.cigame.model.ScoreCard;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;

import org.junit.Test;
import org.jvnet.hudson.test.Bug;

@SuppressWarnings("unchecked")
public class ScoreCardActionTest {

    @Bug(3990)
    @Test 
    public void assertCaseDifferentUserIsReportedAsOneUser() {
        Run<?, ?> build = mock(Run.class);        
        ChangeLogSet changeset = mock(ChangeLogSet.class);
        if(build instanceof AbstractBuild){
            AbstractBuild<?,?> abstractBuild = (AbstractBuild<?,?>)build;
            when(abstractBuild.getChangeSet()).thenReturn(changeset);
        }
        Iterator<Entry> iterator = Arrays.asList(new Entry[]{mockEntry("Name", "OneName"), mockEntry("name", "TwoName")}).iterator();
        when(changeset.iterator()).thenReturn(iterator);
        assertThat(new ScoreCardAction(new ScoreCard(), build).getParticipants(false).size(), is(1));
    }

    @Bug(3990)
    @Test 
    public void assertCaseDifferentUserIsNotReportedAsOneUser() {
        Run<?, ?> build = mock(Run.class);        
        ChangeLogSet changeset = mock(ChangeLogSet.class);
        if(build instanceof AbstractBuild){
            AbstractBuild<?,?> abstractBuild = (AbstractBuild<?,?>)build;
            when(abstractBuild.getChangeSet()).thenReturn(changeset);
        }
        Iterator<Entry> iterator = Arrays.asList(new Entry[]{mockEntry("Name", "OneName"), mockEntry("name", "TwoName")}).iterator();
        when(changeset.iterator()).thenReturn(iterator);
        assertThat(new ScoreCardAction(new ScoreCard(), build).getParticipants(true).size(), is(2));
    }
    
    @Test 
    public void assertParticipantListIsSorted() {
        Run<?, ?> build = mock(Run.class);        
        ChangeLogSet changeset = mock(ChangeLogSet.class);
        if(build instanceof AbstractBuild){
            AbstractBuild<?,?> abstractBuild = (AbstractBuild<?,?>)build;
            when(abstractBuild.getChangeSet()).thenReturn(changeset);
        }
        Iterator<Entry> iterator = Arrays.asList(new Entry[]{mockEntry("one", "David"), mockEntry("two", "Barney"), mockEntry("three", "charlie")}).iterator();
        if(build instanceof AbstractBuild){
            AbstractBuild<?,?> abstractBuild = (AbstractBuild<?,?>)build;
            when(abstractBuild.getChangeSet()).thenReturn(changeset);
        }
        Iterator<User> participantsIterator = new ScoreCardAction(new ScoreCard(), build).getParticipants(true).iterator();
        assertThat(participantsIterator.next().getDisplayName(), is("Barney"));
        assertThat(participantsIterator.next().getDisplayName(), is("charlie"));
        assertThat(participantsIterator.next().getDisplayName(), is("David"));
    }
    
    private Entry mockEntry(String id, String displayName) {
        User user = mock(User.class);
        when(user.getProperty(UserScoreProperty.class)).thenReturn(new UserScoreProperty(3d, true, null));
        when(user.getDisplayName()).thenReturn(displayName);
        when(user.getId()).thenReturn(id);
        Entry entry = mock(Entry.class);
        when(entry.getAuthor()).thenReturn(user);
        return entry;
    }
}
