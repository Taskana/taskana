import { Component, OnInit, Input, ChangeDetectionStrategy, Output,
  EventEmitter, SimpleChanges, OnChanges, ChangeDetectorRef } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'taskana-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
})
export class TaskListComponent implements OnInit {
  @Input()
  tasks: Array<Task>;

  @Input()
  selectedId: string;

  @Output()
  selectedIdChange = new EventEmitter<string>();

  constructor(private router: Router,
              private route: ActivatedRoute,
              private workplaceService: WorkplaceService) {
  }

  ngOnInit() {
  }

  selectTask(taskId: string) {
    this.workplaceService.selectObjectReference();
    this.selectedId = taskId;
    this.selectedIdChange.emit(taskId);
    this.router.navigate([{ outlets: { detail: `taskdetail/${this.selectedId}` } }], { relativeTo: this.route });
  }
}
