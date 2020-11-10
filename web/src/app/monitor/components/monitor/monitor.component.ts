import { Component, OnDestroy, OnInit, Input } from '@angular/core';

@Component({
  selector: 'taskana-monitor',
  templateUrl: './monitor.component.html',
  styleUrls: ['./monitor.component.scss']
})
export class MonitorComponent implements OnInit, OnDestroy {
  @Input() selectedTab = '';

  ngOnInit(): void {}

  ngOnDestroy(): void {}
}
