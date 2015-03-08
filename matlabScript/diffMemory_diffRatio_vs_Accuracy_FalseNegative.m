function diffMemory_diffRatio_vs_Accuracy_FalseNegative()

    data=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\memorysize_sampleRatio_VS_accuracy_FN\diffMemory_vs_samplehold_replacement.txt');

    %size
    sizeData=size(data);
    rows=sizeData(1);

    %one tenth memory
    tenthMem=data(1,2);
    
    accuracy(1,1,1)=1;
    accuracySD(1,1,1)=1;
    fn(1,1,1)=1;
    fnSD(1,1,1)=1;
    for i=1:rows
        isRp=data(i,1)+1;
        brTime=log2(data(i,8))+1;
        ithMem=ithMemory(tenthMem, data(i,2));
        accuracy(isRp, brTime, ithMem)=data(i,3);
        accuracySD(isRp, brTime, ithMem)=data(i,4);
        fn(isRp, brTime, ithMem)=data(i,5);
        fnSD(isRp, brTime, ithMem)=data(i,6);
    end

    colors={'r', 'g', 'b', 'c', 'm'};
    
    %accuracy
    figure
    hold on;
    errorbar(1:5, accuracy(1,1,:), accuracySD(1,1,:), '-.rv', 'linewidth', 1);
    errorbar(1:5, accuracy(1,2,:), accuracySD(1,2,:), '-.g>', 'linewidth', 1);
    
    errorbar(1:5, accuracy(2,1,:), accuracySD(2,1,:), '-rv', 'linewidth', 1);
    errorbar(1:5, accuracy(2,2,:), accuracySD(2,2,:), '-g>', 'linewidth', 1);
    %errorbar(1:5, accuracy(1,3,:), accuracySD(1,3,:), '-.b^', 'linewidth', 1);
    errorbar(1:5, accuracy(2,3,:), accuracySD(2,3,:), '-b^', 'linewidth', 1);
    %errorbar(1:5, accuracy(1,4,:), accuracySD(1,4,:), '--m+', 'linewidth', 1);
    errorbar(1:5, accuracy(2,4,:), accuracySD(2,4,:), '-m+', 'linewidth', 1);
    %errorbar(1:5, accuracy(1,5,:), accuracySD(1,5,:), '-.mo', 'linewidth', 1);
    %errorbar(1:5, accuracy(2,5,:), accuracySD(2,5,:), '-mo', 'linewidth', 1);

    set(gca, 'FontSize', 18,'XTick', 1:5, 'xticklabel', {20604, 51510, 103020, 154530, 206040});
    legend('SH-1', 'SH-2', 'Replace-1', 'Replace-2', 'Replace-4', 'Replace-8', 'Replace-16');
    legend('Location', 'southeast');
    legend('boxoff');
    xlabel('#buckets in hashmap')
    ylabel('accuracy')
    xlim([0.95,5.05])
    ylim([0.73,0.97])
    hold off;
    
    
    %false negative
    figure
    hold on;
    errorbar(1:5, fn(1,1,:), fnSD(1,1,:), '-.rv', 'linewidth', 1);
    errorbar(1:5, fn(1,2,:), fnSD(1,2,:), '-.g>', 'linewidth', 1);
    
    errorbar(1:5, fn(2,1,:), fnSD(2,1,:), '-rv', 'linewidth', 1);
    errorbar(1:5, fn(2,2,:), fnSD(2,2,:), '-g>', 'linewidth', 1);
    %errorbar(1:5, accuracy(1,3,:), accuracySD(1,3,:), '-.b^', 'linewidth', 1);
    errorbar(1:5, fn(2,3,:), fnSD(2,3,:), '-b^', 'linewidth', 1);
    %errorbar(1:5, accuracy(1,4,:), accuracySD(1,4,:), '--m+', 'linewidth', 1);
    errorbar(1:5, fn(2,4,:), fnSD(2,4,:), '-m+', 'linewidth', 1);
    %errorbar(1:5, accuracy(1,5,:), accuracySD(1,5,:), '-.mo', 'linewidth', 1);
    %errorbar(1:5, fn(2,5,:), fnSD(2,5,:), '-mo', 'linewidth', 1);

    set(gca, 'FontSize', 18,'XTick', 1:5,'YTick', 0.01:0.02:0.15, 'xticklabel', {20604, 51510, 103020, 154530, 206040});
    legend('SH-1', 'SH-2', 'Replace-1', 'Replace-2', 'Replace-4', 'Replace-8', 'Replace-16');
    legend('Location', 'northeast');
    legend('boxoff');
    xlabel('#buckets in hashmap')
    ylabel('false negative')
    xlim([0.95,5.05])
    ylim([0.01,0.13])
    hold off;
end

function idx=ithMemory(tenthMem, mem)
    if tenthMem == mem
        idx=1;
        return;
    end
    times=mem/tenthMem;
    idx=int16(times/2.5)+1;
end
