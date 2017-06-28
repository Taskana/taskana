import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-categorieslist',
  templateUrl: './categorieslist.component.html',
  styleUrls: ['./categorieslist.component.css']
})
export class CategorieslistComponent implements OnInit {

  categories = [ {
    "id": "1",
    "name": "Category 1",
    "description": "Das ist die erste Business Category."
  },
  {
    "id": "2",
    "name": "Category 2",
    "description": "Das ist die erste Business Category."
  },
  {
    "id": "3",
    "name": "Category 3",
    "description": "Das ist die erste Business Category."
  }
  ];

  constructor() { }

  ngOnInit() {}

}
