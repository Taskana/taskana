package pro.taskana.impl;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.TaskSummary;
import pro.taskana.model.Workbasket;
import pro.taskana.model.mappings.SummaryMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

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

    @Mock
    private WorkbasketServiceImpl workbasketServiceMock;

    @Test
    public void testGetTaskSummariesByWorkbasketIdWithInternalException() throws WorkbasketNotFoundException {
        // given - set behaviour and expected result
        String workbasketId = "1";
        List<TaskSummary> expectedResultList = new ArrayList<>();
        doNothing().when(taskanaEngineImplMock).openConnection();
        doThrow(new IllegalArgumentException("Invalid ID: " + workbasketId)).when(summaryMapperMock).findTasksummariesByWorkbasketId(workbasketId);
        doNothing().when(taskanaEngineImplMock).returnConnection();
        doReturn(workbasketServiceMock).when(taskanaEngineImplMock).getWorkbasketService();
        doReturn(new Workbasket()).when(workbasketServiceMock).getWorkbasket(any());

        // when - make the call
        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketId(workbasketId);

        // then - verify external communications and assert result
        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(summaryMapperMock, times(1)).findTasksummariesByWorkbasketId(workbasketId);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verify(taskanaEngineImplMock, times(1)).getWorkbasketService();
        verify(workbasketServiceMock, times(1)).getWorkbasket(any());

        verifyNoMoreInteractions(summaryMapperMock, taskanaEngineImplMock, sqlSessionMock,
                workbasketServiceMock);
        assertThat(actualResultList, equalTo(expectedResultList));
    }

    @Test
    public void testGetTaskSummariesByWorkbasketIdGettingResults() throws WorkbasketNotFoundException {
        String workbasketId = "1";
        List<TaskSummary> expectedResultList = Arrays.asList(new TaskSummary(), new TaskSummary());
        doNothing().when(taskanaEngineImplMock).openConnection();
        doNothing().when(taskanaEngineImplMock).returnConnection();
        doReturn(workbasketServiceMock).when(taskanaEngineImplMock).getWorkbasketService();
        doReturn(new Workbasket()).when(workbasketServiceMock).getWorkbasket(any());
        doReturn(expectedResultList).when(summaryMapperMock).findTasksummariesByWorkbasketId(workbasketId);

        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketId(workbasketId);

        verify(taskanaEngineImplMock, times(1)).getWorkbasketService();
        verify(workbasketServiceMock, times(1)).getWorkbasket(any());
        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(summaryMapperMock, times(1)).findTasksummariesByWorkbasketId(workbasketId);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(summaryMapperMock, taskanaEngineImplMock, sqlSessionMock, workbasketServiceMock);
        assertThat(actualResultList, equalTo(expectedResultList));
        assertThat(actualResultList.size(), equalTo(expectedResultList.size()));
    }

    @Test
    public void testGetTaskSummariesByWorkbasketIdGettingNull() throws WorkbasketNotFoundException {
        String workbasketId = "1";
        List<TaskSummary> expectedResultList = new ArrayList<>();
        doNothing().when(taskanaEngineImplMock).openConnection();
        doNothing().when(taskanaEngineImplMock).returnConnection();
        doReturn(null).when(summaryMapperMock).findTasksummariesByWorkbasketId(workbasketId);
        doReturn(workbasketServiceMock).when(taskanaEngineImplMock).getWorkbasketService();
        doReturn(new Workbasket()).when(workbasketServiceMock).getWorkbasket(any());

        List<TaskSummary> actualResultList = cut.getTaskSummariesByWorkbasketId(workbasketId);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(summaryMapperMock, times(1)).findTasksummariesByWorkbasketId(workbasketId);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verify(taskanaEngineImplMock, times(1)).getWorkbasketService();
        verify(workbasketServiceMock, times(1)).getWorkbasket(any());
        verifyNoMoreInteractions(summaryMapperMock, taskanaEngineImplMock, sqlSessionMock,
                workbasketServiceMock);

        assertThat(actualResultList, equalTo(expectedResultList));
        assertThat(actualResultList.size(), equalTo(expectedResultList.size()));
    }
}
