import {layers, fontFamily} from './style';
import {makeCommon} from './main.common';

import {
  StyleSheet,
} from 'react-native';

export let styles = {};

export const init = (config) => {
    const modes = ['normal', 'compact', 'tiny']
    let mode = modes.includes(config.compact)? config.compact: 'compact'; // normal, compact, tiny
    const iconSize = () => {
        return {
            normal: 32,
            compact: 28,
            tiny: 24
        }[mode];
    };
    const fontSize = () => {
        return {
            normal: 16,
            compact: 14,
            tiny: 12
        }[mode];
    };
    const fontSizeSmall = () => {
        return {
            normal: 14,
            compact: 12,
            tiny: 10
        }[mode];
    };
    const popupTop = () => {
        return {
            normal: 70,
            compact: 60,
            tiny: 50
        }[mode];
    };
    const [_layers, _colorDefs] = layers();
    const _fontFamily = 'monospace';
    const _styles = {
        "vflex": {
            flexDirection: 'column',
        },
        "hflex": {
            flexDirection: 'row',
            alignItems: 'flex-start',
        },
        "flex1": {
            flex: 1,
        },
        "flex1s": {
            flex: 1,
        },
        "flex0": {
            flex: 0,
        },
        "spacer": {
            flex: 1,
        },
        "vproxy": {
            flex: 1,
            flexDirection: 'column',
        },
        "hproxy": {
            flex: 1,
            flexDirection: 'row',
        },
        app: {
            backgroundColor: _layers.bg.bg,
        },
        toolbar: {
            height: 56,
        },
        input_box: {
            position: 'absolute',
            marginTop: popupTop(),
            left: 3,
            right: 3,
            top: 0,
            borderWidth: 2,
            borderStyle: 'solid',
            borderColor: _layers.bg.br,
            backgroundColor: _layers.bg.bg,
            padding: 3,
        },
        modal_dialog: {
            position: 'absolute',
            flexDirection: 'column',
            left: 0,
            top: 0,
            right: 0,
            bottom: 0,
            alignItems: 'center',
            justifyContent: 'center',
            backgroundColor: '#88888822',
        },
        menu: {
            position: 'relative',
        },
        menu_popup: {
            position: 'absolute',
            top: 0,
            right: iconSize(),
        },
        modal_small: {
            position: 'absolute',
            left: 7,
            right: 7,
            top: 7,
            bottom: 7,
        },
        modal_large: {
            width: 400,
            height: 400,
        },
        modal_inner: {
            borderWidth: 2,
            borderStyle: 'solid',
            borderColor: _layers.bg.br,
            backgroundColor: _layers.bg.bg,
        },
        input_narrow: {
            alignItems: 'stretch',
        },
        input_text: {
            margin: 3,
        },
        btn: {
            justifyContent: 'center',
            alignItems: 'center',
            padding: 0,
            margin: 0,
        },
        icon: {
            width: iconSize(),
            height: iconSize(),
        },
        text: {
            fontSize: fontSize(),
            fontFamily: _fontFamily,
            color: _layers.l1.fg,
        },
        inp: {
            fontSize: fontSize(),
            fontFamily: _fontFamily,
            borderLeftWidth: 2,
            borderStyle: 'solid',
            borderColor: _layers.l2.fg,
            backgroundColor: _layers.l2.bg,
            color: _layers.l2.fg,
            height: iconSize(),
            lineHeight: iconSize(),
            padding: 5,
            margin: 3,
            alignSelf: 'stretch',
        },
        description: {
            alignSelf: 'center',
        },
        textSmall: {
            fontSize: fontSizeSmall(),
        },
        annotation_line: {
            marginLeft: iconSize(),
        },
        one_task: {
            marginTop: 0,
            marginBottom: 5,
        },
        one_item: {
            backgroundColor: _layers.l1.bg,
        },
        task_selected: {
            backgroundColor: _layers.i_sel.bg,
        },
        reports: {
            borderStyle: 'solid',
            borderColor: _layers.bg.br,
            borderWidth: 1,
            borderRightWidth: 1,
        },
        navigation: {
            borderStyle: 'solid',
            borderColor: _layers.bg.br,
            borderWidth: 1,
            borderLeftWidth: 1,
        },

        one_nav: {
            backgroundColor: _layers.l1.bg,
            marginLeft: 3,
            marginRight: 3,
            marginBottom: 5,
            padding: 5,
        },

        hilite: {
            backgroundColor: _layers.l2.bg,
        },
        oneLine: {
        },
        left_dock: {
            marginLeft: _layers.panes.left.width,
        },
        right_dock: {
            marginRight: _layers.panes.right.width,
        },
        none: {
            width: 0,
            position: 'absolute',
        },
        paneBody: {
            margin: 3,          
        },
        pane: {
            position: 'relative',
        },
        loadingIndicator: {
            position: 'absolute',
            left: 0,
            right: 0,
            top: popupTop(),
            alignItems: 'center',
        },
        float_pane: {
            position: 'absolute',
            left: 0,
            top: 0,
            bottom: 0,
            right: 0,
            flexDirection: 'row',
            alignItems: 'stretch',
        },
        float_space: {
            backgroundColor: '#88888810',
        },
        profile_item: {
        },
        calendar_week: {
            justifyContent: 'space-between',
            marginLeft: 3,
        },
        calendar_day: {
            padding: 3,
        },
        calendar_passive: {
            opacity: 0.3,
        },
        calendar_weekend: {
            backgroundColor: _layers.l2.bg,
            color: _layers.l2.fg,
        },
    };
    let __styles = makeCommon({});
    for (var key in _styles) {
        __styles[key] = _styles[key];
    };
    styles = StyleSheet.create(__styles);
    styles.multiline = {
        rows: parseInt(config['multiline.rows'] || '3'),
    };
};

export const _l = (...args) => {
    let arr = args;
    if (arr.length == 1 && Array.isArray(arr[0])) { // Convert
        return arr[0];
    };
    return args;
};
