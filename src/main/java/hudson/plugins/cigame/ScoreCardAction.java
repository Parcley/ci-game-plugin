package hudson.plugins.cigame;

import hudson.plugins.cigame.util.ChangeSetAuthors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import hudson.model.Run;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.cigame.model.ScoreCard;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import java.util.Set;

/**
 * Score card for a certain build
 * 
 * @author Erik Ramfelt
 */
@ExportedBean(defaultVisibility = 999)
public class ScoreCardAction implements Action {

    private static final long serialVersionUID = 1L;

    private Run<?, ?> build;

    private ScoreCard scorecard;

    public ScoreCardAction(ScoreCard scorecard, Run<?, ?> b) {
        build = b;
        this.scorecard = scorecard;
    }

    public Run<?, ?> getBuild() {
        return build;
    }

    public String getDisplayName() {
        return Messages.Scorecard_Title(); //$NON-NLS-1$
    }

    public String getIconFileName() {
        return GameDescriptor.ACTION_LOGO_MEDIUM;
    }

    public String getUrlName() {
        return "cigame"; //$NON-NLS-1$
    }

    @Exported
    public ScoreCard getScorecard() {
        return scorecard;
    }

    @Exported
    public Collection<User> getParticipants() {
        return getParticipants(Hudson.getInstance().getDescriptorByType(GameDescriptor.class).getNamesAreCaseSensitive());
    }
    
    Collection<User> getParticipants(boolean usernameIsCasesensitive) {
        Comparator<User> userIdComparator = new CaseInsensitiveUserIdComparator();
        List<User> players = new ArrayList<User>();
        Set<User> usersInBuild = ChangeSetAuthors.getChangeSetAuthors(build);
        
        for (User user : usersInBuild) {
            UserScoreProperty property = user.getProperty(UserScoreProperty.class);
            if ((property != null) 
                    && property.isParticipatingInGame() 
                    && (usernameIsCasesensitive || Collections.binarySearch(players, user, userIdComparator) < 0)) {
                players.add(user);
            }
        }
        Collections.sort(players, new UserDisplayNameComparator());
        return players;
    }
    
    private static class UserDisplayNameComparator implements Comparator<User> {
        public int compare(User arg0, User arg1) {
            return arg0.getDisplayName().compareToIgnoreCase(arg1.getDisplayName());
        }            
    }
}
