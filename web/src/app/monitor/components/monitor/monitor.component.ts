import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'taskana-monitor',
  templateUrl: './monitor.component.html',
  styleUrls: ['./monitor.component.scss']
})
export class MonitorComponent implements OnInit {
  selectedTab = '';

  ngOnInit(): void {
    this.selectedTab = 'tasks-priority';
  }
}
