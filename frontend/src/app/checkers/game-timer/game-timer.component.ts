import {Component, ViewChild} from '@angular/core';

@Component({
  selector: 'app-game-timer',
  templateUrl: './game-timer.component.html',
  styleUrls: ['./game-timer.component.scss']
})
export class GameTimerComponent {

  @ViewChild(GameTimerComponent, {static: true}) timer: GameTimerComponent;
  startTime: number = 0;
  interval;
  hours: string = "00";
  minutes: string = "00";
  seconds: string = "00";

  constructor() {
  }

  startTimer() {
    this.interval = setInterval(() => {
      this.startTime++;
      this.formatTimer(this.startTime);
    }, 1000)
  }

  pauseTimer() {
    clearInterval(this.interval);
  }

  private formatTimer(startTime: number) {
    let hours = Math.floor(startTime / 3600);
    let minutes = Math.floor((startTime - (hours * 3600)) / 60);
    let seconds = startTime - (hours * 3600) - (minutes * 60);

    if (hours < 10) {
      this.hours = "0" + hours;
    } else {
      this.hours = String(hours);
    }
    if (minutes < 10) {
      this.minutes = "0" + minutes;
    } else {
      this.minutes = String(minutes);
    }
    if (seconds < 10) {
      this.seconds = "0" + seconds;
    } else {
      this.seconds = String(seconds);
    }
  }
}
