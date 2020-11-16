import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-workbasket-distribution-targets-list-dialog',
  templateUrl: './workbasket-distribution-targets-list-dialog.component.html',
  styleUrls: ['./workbasket-distribution-targets-list-dialog.component.scss']
})
export class WorkbasketDistributionTargetsListDialogComponent implements OnInit {
  toolbarState = false;

  constructor(
    public dialogRef: MatDialogRef<WorkbasketDistributionTargetsListDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data
  ) {
    console.log(data.distributionTargets);
  }

  ngOnInit() {}

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }
}
