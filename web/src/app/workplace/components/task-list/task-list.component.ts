import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'taskana-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent implements OnInit {
  @Input()
  tasks: Task[];

  @Input()
  selectedId: string;

  @Output()
  selectedIdChange = new EventEmitter<string>();

  constructor(private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {}

  selectTask(taskId: string) {
    this.selectedId = taskId;
    this.selectedIdChange.emit(taskId);
    this.router.navigate([{ outlets: { detail: `taskdetail/${this.selectedId}` } }], { relativeTo: this.route });
  }
}
