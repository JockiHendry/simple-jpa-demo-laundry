/*
 * Copyright 2014 Jocki Hendry.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package util;

import griffon.core.UIThreadManager;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;

class BusyLayerUI extends LayerUI<JPanel> implements ActionListener {

    public static final BusyLayerUI instance = new BusyLayerUI();
    public static BusyLayerUI getInstance() {
        return instance;
    }

    private final int RADIUS = 150;
    private final Color WARNA_LATAR = new Color(219, 247, 186);
    private final Color WARNA_PROGRESS = new Color(252, 192, 0);
    private final Color WARNA_BAYANGAN = new Color(61, 255, 60, 255);

    private boolean visible = false;
    private BufferedImage pixelTexture;
    private Rectangle2D ukuranTexture;
    private Arc2D.Double fullCircle, progress;
    private Timer timer;

    private BusyLayerUI() {
        pixelTexture = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        ukuranTexture = new Rectangle2D.Double(0, 0, 2, 2);
        Graphics2D g2 = pixelTexture.createGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 1, 2);
        g2.fill(ukuranTexture);
        g2.setColor(Color.GRAY);
        g2.fillRect(1, 0, 1, 2);
        g2.dispose();
        pixelTexture.flush();

        progress = new Arc2D.Double(50, 50, 400, 400, 0, 0, Arc2D.OPEN);
        fullCircle = new Arc2D.Double(50, 50, 400, 400, 0, -360, Arc2D.OPEN);

    }

    void show() {
        if (visible) return;
        UIThreadManager.getInstance().executeSync(new Runnable() {
            public void run() {
                progress.setAngleExtent(0);
                timer = new Timer(1000/24, BusyLayerUI.this);
                timer.start();
                visible = true;
                firePropertyChange("visible", false, true);
            }
        });
    }

    void hide() {
        if (!visible) return;
        UIThreadManager.getInstance().executeSync(new Runnable() {
            public void run() {
                visible = false;
                firePropertyChange("visible", true, false);
            }
        });
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        if (!visible) return;

        int w = c.getWidth();
        int h = c.getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Composite currentComposite = g2.getComposite();

        // Buat layar terlihat seperti tidak aktif (lebih gelap dan kabur)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.setPaint(new GradientPaint(0, 0, Color.BLACK, 0, h, Color.GRAY));
        g2.fillRect(0, 0, w, h);

        double centerX = (double) (w/2);
        double centerY = (double) (h/2);

        // Buat lingkaran terang
        g2.setStroke(new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(WARNA_LATAR);
        fullCircle.setFrameFromCenter(centerX, centerY, centerX+RADIUS, centerY+RADIUS);
        g2.draw(fullCircle);

        // Buat progress yang menandakan program sedang sibuk
        g2.setColor(WARNA_PROGRESS);
        progress.setFrameFromCenter(centerX, centerY, centerX+RADIUS, centerY+RADIUS);
        g2.draw(progress);
        g2.setColor(WARNA_BAYANGAN);
        g2.setStroke(new BasicStroke(30, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(progress);

        // Selesai
        g2.setComposite(currentComposite);
        g2.dispose();
    }

    @Override
    public void applyPropertyChange(PropertyChangeEvent evt, JLayer<? extends JPanel> l) {
        if ("tick".equals(evt.getPropertyName()) || "visible".equals(evt.getPropertyName())) {
            l.repaint();
        }
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        ((JLayer)c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent c) {
        ((JLayer)c).setLayerEventMask(0);
        super.uninstallUI(c);
    }

    @Override
    public void eventDispatched(AWTEvent e, JLayer<? extends JPanel> l) {
        if (visible && e instanceof InputEvent) {
            ((InputEvent)e).consume();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (visible) {
            double extend = progress.getAngleExtent();
            if (extend <= -360) {
                extend = 0;
            } else {
                extend -= 3;
            }
            progress.setAngleExtent(extend);
            firePropertyChange("tick", 0, 1);
        } else {
            timer.stop();
        }
    }
}
