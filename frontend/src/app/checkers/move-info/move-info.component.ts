import {Component, Input, ViewChild} from '@angular/core';
import {GameTimerComponent} from "../game-timer/game-timer.component";
import {Subject} from "rxjs";
import {Game} from "../domain/game";

@Component({
  selector: 'app-move-info',
  templateUrl: './move-info.component.html',
  styleUrls: ['./move-info.component.scss']
})
export class MoveInfoComponent {

  @Input() game: Game;
  @Input() gamePaused: Subject<boolean>;
  @ViewChild(GameTimerComponent, {static: true}) timer: GameTimerComponent;

  constructor() {
  }

  pauseGame() {
    this.timer.pauseTimer();
    this.gamePaused.next(true);
  }

  unpauseGame() {
    this.timer.startTimer();
    this.gamePaused.next(false);
  }

  startTimer() {
    this.timer.startTimer();
    this.gamePaused.next(false);
  }

  startNewGame() {
    window.location.reload();
  }

  gameFinished() {
    if (this.game && this.game.status === 'FINISHED') {
      this.pauseGame();
      return true
    }
    return false;
  }
}
