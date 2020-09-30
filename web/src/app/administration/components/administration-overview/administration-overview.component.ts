import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'taskana-administration-overview',
  templateUrl: './administration-overview.component.html',
  styleUrls: ['./administration-overview.component.scss']
})
export class AdministrationOverviewComponent implements OnInit {
  @Input() selectedTab = '';

  constructor(private router: Router) {}

  ngOnInit() {
    const urlPaths = this.router.url.split('/');
    this.selectedTab = urlPaths[urlPaths.length - 1];
  }
}
