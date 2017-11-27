    import { NgModule } from '@angular/core';
    import { RouterModule, Routes } from '@angular/router';
    import { AppComponent } from './app.component';
    import { TaskComponent } from './task/task.component';
    import { TasksComponent } from './tasks/tasks.component';

    const appRoutes: Routes = [
      {
        path: 'tasks',
        component: TasksComponent
      },
      {
        path: 'tasks/:id',
        component: TaskComponent
      },
      {
          path: '',
          redirectTo: 'tasks',
          pathMatch: 'full'
      },
    ];
    @NgModule({
      imports: [
        RouterModule.forRoot(
          appRoutes
        )
      ],
      exports: [
        RouterModule
      ]
    })
    export class AppRoutingModule {}
