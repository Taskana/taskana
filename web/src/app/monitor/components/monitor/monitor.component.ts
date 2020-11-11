import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'taskana-monitor',
  templateUrl: './monitor.component.html',
  styleUrls: ['./monitor.component.scss']
})
export class MonitorComponent implements OnInit {
  @Input() selectedTab = '';

  constructor(public router: Router) {
    this.router.navigate(['/taskana/monitor/tasks']);
  }

  ngOnInit(): void {
    this.selectedTab = 'tasks';
  }
}
