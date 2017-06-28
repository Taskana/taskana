import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-categoriesadministration',
  templateUrl: './categoriesadministration.component.html',
  styleUrls: ['./categoriesadministration.component.css']
})
export class CategoriesadministrationComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  @Input()
  categorySelected: any;

  onCategorySelected(arg) {
    console.log("Event angekommen: ",arg);
    this.categorySelected = arg;
  }

}
