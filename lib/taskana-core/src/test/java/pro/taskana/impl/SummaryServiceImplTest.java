package pro.taskana.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.model.TaskSummary;
import pro.taskana.model.mappings.SummaryMapper;

/**
 * Testing the {@link SummaryServiceImpl} component.
 * Mocks are initialized before each Method by @Runner-Annotation.
 */
@RunWith(MockitoJUnitRunner.class)
public class SummaryServiceImplTest {

    @InjectMocks
    private SummaryServiceImpl cut;

    @Mock
    private SummaryMapper summaryMapperMock;

    @Mock
    private TaskanaEngineImpl taskanaEngineImplMock;

    @Mock
    private SqlSession sqlSessionMock;

    @Test
    public void testGetTaskSummariesByWorkbasketIdWithInternalException() {
        // given - set behaviour and expected result
        String workbasketId = "1";
        List<TaskSummary> expectedResultList = new ArrayList<>();
        doNothing().when(taskanaEngineImplMock).openConnection();
        doThrow(new IllegalArgumentException("Invalid ID: " + workbasketId)).when(summaryMapperMock).findTasksummariesByWorkbasketId(workbasketId);
        doNothing().when(taskanaEngineImplMock).returnConnection();

        // when - make the call
        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketId(workbasketId);

        // then - verify external communications and assert result
        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(summaryMapperMock, times(1)).findTasksummariesByWorkbasketId(workbasketId);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(summaryMapperMock, taskanaEngineImplMock, sqlSessionMock);
        assertThat(actualResultList, equalTo(expectedResultList));
    }

    @Test
    public void testGetTaskSummariesByWorkbasketIdGettingResults() {
        String workbasketId = "1";
        List<TaskSummary> expectedResultList = Arrays.asList(new TaskSummary(), new TaskSummary());
        doNothing().when(taskanaEngineImplMock).openConnection();
        doNothing().when(taskanaEngineImplMock).returnConnection();
        doReturn(expectedResultList).when(summaryMapperMock).findTasksummariesByWorkbasketId(workbasketId);

        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketId(workbasketId);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(summaryMapperMock, times(1)).findTasksummariesByWorkbasketId(workbasketId);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(summaryMapperMock, taskanaEngineImplMock, sqlSessionMock);
        assertThat(actualResultList, equalTo(expectedResultList));
        assertThat(actualResultList.size(), equalTo(expectedResultList.size()));
    }

    @Test
    public void testGetTaskSummariesByWorkbasketIdGettingNull() {
        String workbasketId = "1";
        List<TaskSummary> expectedResultList = new ArrayList<>();
        doNothing().when(taskanaEngineImplMock).openConnection();
        doNothing().when(taskanaEngineImplMock).returnConnection();
        doReturn(null).when(summaryMapperMock).findTasksummariesByWorkbasketId(workbasketId);

        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketId(workbasketId);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(summaryMapperMock, times(1)).findTasksummariesByWorkbasketId(workbasketId);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(summaryMapperMock, taskanaEngineImplMock, sqlSessionMock);
        assertThat(actualResultList, equalTo(expectedResultList));
        assertThat(actualResultList.size(), equalTo(expectedResultList.size()));
    }
}
