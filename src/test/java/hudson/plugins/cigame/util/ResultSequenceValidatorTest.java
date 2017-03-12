package hudson.plugins.cigame.util;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import hudson.model.Run;
import hudson.model.Result;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class ResultSequenceValidatorTest {

    @Test
    public void assertResultBelowThresholdIsNotValidated() {
        Run build = mock(Run.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        assertThat(new ResultSequenceValidator(Result.SUCCESS, 1).isValid(build), is(false));
    }
    
    @Test
    public void assertShortSequenceIsNotValidated() {
        Run build = mock(Run.class);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(build.getPreviousBuild()).thenReturn(null);
        assertThat(new ResultSequenceValidator(Result.SUCCESS, 2).isValid(build), is(false));
    }
    
    @Test
    public void assertLastBuildIsBelowThresholdIsNotValidated() {
        Run build = mock(Run.class);
        Run previousBuild = mock(Run.class);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(previousBuild.getResult()).thenReturn(Result.UNSTABLE);
        assertThat(new ResultSequenceValidator(Result.SUCCESS, 2).isValid(build), is(false));
    }
    
    @Test
    public void assertBuildSequenceAboveThresholdIsValidated() {
        Run build = mock(Run.class);
        Run previousBuild = mock(Run.class);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(previousBuild.getResult()).thenReturn(Result.UNSTABLE);
        assertThat(new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build), is(true));
    }
}
