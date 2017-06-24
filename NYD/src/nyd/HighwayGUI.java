package nyd;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class HighwayGUI {
	private int num_lanes = 6;
	private int num_positions = 17;
	private int hp_size_x = 300;
	private int hp_size_y = 900;
	
	private HighwayPanel highway_panel;
	
	private HighwayWorld world;
	private JPanel[][] panel_grid;
	
	
	public HighwayGUI(HighwayWorld world){
		this.world = world;
	}

	public void start(){
		NYDFrame nyd_frame = new NYDFrame();
		nyd_frame.setVisible(true);
	}

    public static void main(String[] args){
        HighwayGUI highway = new HighwayGUI(null);
        highway.start();
    }
	
	private class NYDFrame extends JFrame{
		public NYDFrame(){
			super();
			
			this.setSize(hp_size_x, hp_size_y);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			JPanel nyd_panel = new JPanel();
			nyd_panel.setSize(hp_size_x, hp_size_y);
			nyd_panel.setBackground(Color.red);
			nyd_panel.setLayout(new GridLayout(1, 2));

			highway_panel = new HighwayPanel();
			nyd_panel.add(highway_panel);
			
			
			this.getContentPane().add(nyd_panel);

            highway_panel.update();
		}
	}
	
	private class HighwayPanel extends JPanel{
        ImageIcon road = new ImageIcon("img/road.png");
        ImageIcon shoulder_left = new ImageIcon("img/shoulder_left.png");
        ImageIcon shoulder_right = new ImageIcon("img/shoulder_right.png");
        ImageIcon car_white = new ImageIcon("img/car_white.png");
        ImageIcon car_blue = new ImageIcon("img/car_blue.png");
        ImageIcon car_red = new ImageIcon("img/car_red.png");
        ImageIcon boom = new ImageIcon("img/boom.png");
        ImageIcon box_yellow = new ImageIcon("img/box_yellow.png");
        
		
		public HighwayPanel(){
			super();
			
			this.setSize(hp_size_x, hp_size_y);
			this.setLayout(new GridLayout(num_positions, num_lanes));
			
			panel_grid = new JPanel[num_lanes][num_positions];
			for(int j = num_positions - 1; j >= 0; j--){
				for(int i = 0; i < num_lanes; i++){
					
					if(i == 0){
						panel_grid[i][j] = new JPanel(){
							protected void paintComponent(Graphics g) {  
				                g.drawImage(shoulder_left.getImage(), 0, 0, shoulder_left.getIconWidth(),  
				                        shoulder_left.getIconHeight(), shoulder_left.getImageObserver());  
				            }  
						};
                    }else if(i == num_lanes - 1){
						panel_grid[i][j] = new JPanel(){
							protected void paintComponent(Graphics g) {  
				                g.drawImage(shoulder_right.getImage(), 0, 0, shoulder_right.getIconWidth(),  
				                        shoulder_right.getIconHeight(), shoulder_right.getImageObserver());  
				            }  
						};
                    }else{
						panel_grid[i][j] = new JPanel(){
							protected void paintComponent(Graphics g) {  
				                g.drawImage(road.getImage(), 0, 0, road.getIconWidth(),  
				                        road.getIconHeight(), road.getImageObserver());  
				            }  
						};
                    }
					
					
					panel_grid[i][j].setBackground(Color.black);
					this.add(panel_grid[i][j]);
				}
			}
		}
		
		public void update(){
			Truck[][] truck_grid = world.grid;
			int agent_lane = world.agent_lane;
			int agent_position = world.agent_position;
			int gaze_lane = world.gaze_lane;
			int gaze_position = world.gaze_position;

            JLabel car_white_label = new JLabel();
            car_white_label.setIcon(car_white);



            JLabel boom_label = new JLabel();
            boom_label.setIcon(boom);

			for(int i = 0; i < num_lanes; i++){
				for(int j = 0; j < num_positions; j++){
					panel_grid[i][j].setBorder(null);

					Truck t = truck_grid[i][j];
                    
                    panel_grid[i][j].removeAll();
                    panel_grid[i][j].revalidate();
                    panel_grid[i][j].repaint();

					if(t != null){
						if(t.speed > 0){
                            JLabel car_red_label = new JLabel();
                            car_red_label.setIcon(car_red);
							panel_grid[i][j].add(car_red_label);
						}
						else{
                            JLabel car_blue_label = new JLabel();
                            car_blue_label.setIcon(car_blue);
							panel_grid[i][j].add(car_blue_label);
						}
					}
				}
			}

            panel_grid[agent_lane][agent_position].add(car_white_label);
			
			if(world.reward() == -5.0){
				panel_grid[agent_lane][agent_position].removeAll();
                panel_grid[agent_lane][agent_position].revalidate();
                panel_grid[agent_lane][agent_position].repaint();
				panel_grid[agent_lane][agent_position].add(boom_label);
            }
			
		}
		
	}
	
	public void update(){
		highway_panel.update();
		
		try{
			Thread.sleep(500);
		}catch(Exception e){}
	}
}
