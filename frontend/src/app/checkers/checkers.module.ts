import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BoardComponent} from './board/board.component';
import {GameComponent} from './game/game.component';
import {LayoutModule} from '@angular/cdk/layout';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {MatGridListModule} from "@angular/material/grid-list";
import {FlexLayoutModule} from "@angular/flex-layout";
import {GameTimerComponent} from './game-timer/game-timer.component';
import {DragDropModule} from "@angular/cdk/drag-drop";
import {DiskComponent} from './disk/disk.component';
import {MoveInfoComponent} from './move-info/move-info.component';
import {MatCardModule} from "@angular/material/card";
import {MatTooltipModule} from "@angular/material/tooltip";


@NgModule({
  declarations: [BoardComponent,
    GameComponent,
    GameTimerComponent,
    DiskComponent,
    MoveInfoComponent,
  ],
  exports: [
    GameComponent
  ],
  imports: [
    CommonModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatGridListModule,
    FlexLayoutModule,
    DragDropModule,
    MatCardModule,
    MatTooltipModule
  ]
})
export class CheckersModule {
}
