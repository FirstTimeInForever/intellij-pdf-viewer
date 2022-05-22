package application

import csstype.pct
import csstype.px
import emotion.react.Global
import emotion.react.styles
import react.FC
import react.Props
import react.dom.html.ReactHTML

val normalizeStyles = FC<Props> {
  Global {
    styles {
      ReactHTML.html {
        height = 100.pct
      }
      ReactHTML.body {
        margin = 0.px
        padding = 0.px
        height = 100.pct
      }
      "#root" {
        height = 100.pct
      }
    }
  }
}
