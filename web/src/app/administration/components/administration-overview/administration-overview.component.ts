import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'taskana-administration-overview',
  templateUrl: './administration-overview.component.html',
  styleUrls: ['./administration-overview.component.scss']
})
export class AdministrationOverviewComponent implements OnInit {
  @Input() selectedTab = '';

  constructor() {}

  ngOnInit() {}
}
