import { Component, Inject } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-access-items-management-dialog',
  templateUrl: './access-items-management-dialog.component.html',
  styleUrls: ['./access-items-management-dialog.component.scss']
})
export class AccessItemsManagementDialogComponent {
  groups = [];

  constructor(
    public dialogRef: MatDialogRef<AccessItemsManagementDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
    ) { }

  ngOnInit(): void {
    this.groups = this.data;
  }

}
