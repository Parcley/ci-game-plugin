package hudson.plugins.cigame.util;

import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.User;
import hudson.scm.ChangeLogSet;
import java.util.Collection;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;


// adapted from emailext/plugins/recipients/RecipientProviderUtilities.java from email-ext-plugin 
public class ChangeSetAuthors {
    
    public static Set<User> getChangeSetAuthors(final Run<?, ?> run) {
        final Set<User> users = new HashSet<User>();
        if (run instanceof AbstractBuild<?,?>) {
            final ChangeLogSet<?> changeLogSet = ((AbstractBuild<?,?>)run).getChangeSet();
            addChangeSetUsers(changeLogSet, users);
        } else {
            try {
                Method getChangeSets = run.getClass().getMethod("getChangeSets");
                if (List.class.isAssignableFrom(getChangeSets.getReturnType())) {
                    @SuppressWarnings("unchecked")
                    List<ChangeLogSet<ChangeLogSet.Entry>> sets = (List<ChangeLogSet<ChangeLogSet.Entry>>) getChangeSets.invoke(run);
                    if (Iterables.all(sets, Predicates.instanceOf(ChangeLogSet.class))) {
                        for (ChangeLogSet<ChangeLogSet.Entry> set : sets) {
                            addChangeSetUsers(set, users);
                        }
                    }
                }
            } catch (NoSuchMethodException e){
                //console.log("Exception getting changesets for %s: %s", run, e);
            } catch (InvocationTargetException e){
                //console.log("Exception getting changesets for %s: %s", run, e);
            } catch (IllegalAccessException e){
                //console.log("Exception getting changesets for %s: %s", run, e);
            }
        }
        return users;
    }

    
    private static void addChangeSetUsers(ChangeLogSet<?> changeLogSet, Set<User> users) {
        final Set<User> changeAuthors = new HashSet<User>();
        for (final ChangeLogSet.Entry change : changeLogSet) {
            final User changeAuthor = change.getAuthor();
            changeAuthors.add(changeAuthor);
        }
        users.addAll(changeAuthors);
    }
    
}
