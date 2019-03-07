import {
  Component, OnInit, Input, ChangeDetectionStrategy, Output,
  EventEmitter, SimpleChanges, OnChanges, ChangeDetectorRef
} from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'taskana-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
  // This is used to avoid angular detect changes automatically since displayDate is bein executed on every onChange.
  // this cause a low performance in the screen.
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TaskListComponent implements OnInit, OnChanges {

  @Input()
  tasks: Array<Task>;
  @Input()
  selectedId: string;
  @Output()
  selectedIdChange = new EventEmitter<string>();

  constructor(private router: Router,
    private route: ActivatedRoute,
    private workplaceService: WorkplaceService,
    private changeDetector: ChangeDetectorRef) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.tasks || changes.selectedId) {
      this.changeDetector.detectChanges();
    }
  }

  selectTask(taskId: string) {
    this.workplaceService.selectObjectReference(undefined);
    this.selectedId = taskId;
    this.selectedIdChange.emit(taskId);
    this.router.navigate([{ outlets: { detail: `taskdetail/${this.selectedId}` } }], { relativeTo: this.route });
  }

  displayDate(date: string): string {
    return TaskanaDate.getDateToDisplay(date);
  }

}
